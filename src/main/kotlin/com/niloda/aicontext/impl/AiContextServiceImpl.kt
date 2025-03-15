package com.niloda.aicontext.impl

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.niloda.aicontext.impl.AiContextServiceImpl.IntelliJFileAdapter
import com.niloda.aicontext.model.AiContextService
import com.niloda.aicontext.model.IEditor
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IFileEditorManager
import com.niloda.aicontext.model.IProject
import java.util.concurrent.ConcurrentLinkedQueue

object AiContextServiceImpl : AiContextService {

    override val queue = ConcurrentLinkedQueue<AiContextService.QueueItem>()
    private val activeTasks = mutableMapOf<IFile, Pair<Task.Backgroundable, ProgressIndicator>>()
    private val aiSender: AiSender = AiSender()

    class IntelliJProjectAdapter(val project: Project) : IProject {
        override val name: String = project.name
        override val basePath: String? = project.basePath
        override fun getFileEditorManager(): IFileEditorManager = IntelliJFileEditorManagerAdapter(
            project = project,
            manager = FileEditorManager.getInstance(project)
        )
    }

    class IntelliJFileAdapter(val psiFile: PsiFile) : IFile {
        override val name: String = psiFile.name
        override val text: String? = psiFile.text
        override val virtualFilePath: String = psiFile.virtualFile.path
    }

    class IntelliJEditorAdapter(private val editor: Editor) : IEditor {
        override val documentText: String? = editor.document.text
        override val selectedText: String? = editor.selectionModel.selectedText
    }

    class IntelliJFileEditorManagerAdapter(
        private val manager: FileEditorManager,
        private val project: Project
    ) : IFileEditorManager {
        override val openFiles: List<IFile> = manager.openFiles.map { IntelliJFileAdapter(psiFileAdapter(project, it)) }
        override fun getEditors(file: IFile): List<IEditor> {
            val psiFile = (file as? IntelliJFileAdapter)?.psiFile ?: return emptyList()
            return manager.getEditors(psiFile.virtualFile).map { IntelliJEditorAdapter(it as Editor) }
        }
    }

    override fun getContext(project: IProject, editorText: String?, file: IFile?): String {
        return when {
            editorText?.isNotBlank() == true -> editorText
            file != null -> file.text ?: ""
            else -> {
                val fileEditorManager = project.getFileEditorManager()
                val openFiles = fileEditorManager.openFiles
                val textContents = openFiles.mapNotNull { file ->
                    fileEditorManager.getEditors(file).mapNotNull { editor ->
                        editor.documentText
                    }.firstOrNull()
                }
                textContents.joinToString("\n\n---\n\n")
            }
        }
    }

    override fun sendToAi(prompt: String, project: IProject): String? =
        aiSender.sendToAi(prompt, project)

    override fun queueFile(file: IFile) {
        val existingItem = queue.find { it.file == file }
        if (existingItem != null) {
            queue.remove(existingItem)
            if (existingItem.status == AiContextService.QueueItem.Status.RUNNING) {
                terminate(file)
            }
        }
        val item = AiContextService.QueueItem(file)
        queue.add(item)
        println("Queued file: ${file.name}, Queue size: ${queue.size}")
    }

    override fun processFile(item: AiContextService.QueueItem, project: IProject) {
        if (item.status != AiContextService.QueueItem.Status.PENDING) return
        item.status = AiContextService.QueueItem.Status.RUNNING
        item.startTime = System.currentTimeMillis()

        val task = object :
            Task.Backgroundable((project as IntelliJProjectAdapter).project, "Processing ${item.file.name}", true) {
            private lateinit var indicatorRef: ProgressIndicator

            override fun run(indicator: ProgressIndicator) {
                indicatorRef = indicator
                if (indicator.isCanceled) return

                val prompt = item.prompt + (item.file.text ?: "")
                val response = sendToAi(prompt, project)
                item.status =
                    if (response != null) AiContextService.QueueItem.Status.DONE else AiContextService.QueueItem.Status.ERROR
                item.startTime = null
                activeTasks.remove(item.file)
            }

            override fun onCancel() {
                item.status = AiContextService.QueueItem.Status.CANCELLED
                item.startTime = null
                activeTasks.remove(item.file)
            }
        }
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, object : ProgressIndicatorBase() {
            override fun start() {
                super.start()
                activeTasks[item.file] = task to this
            }
        })
    }

    override fun terminate(file: IFile) {
        activeTasks[file]?.second?.cancel()
    }

    override fun getQueueStatus(): List<AiContextService.QueueItem> = queue.toList()
}

// Helper to adapt PsiFile to IFile (since PsiFile isn't directly adaptable due to IntelliJ specifics)
fun psiFileAdapter(project: Project, virtualFile: com.intellij.openapi.vfs.VirtualFile): PsiFile {
    val project = FileEditorManager.getInstance(project).project
    return com.intellij.psi.PsiManager.getInstance(project).findFile(virtualFile)!!
}

fun Project.adapt() = AiContextServiceImpl.IntelliJProjectAdapter(this)
fun PsiFile.adapt() = IntelliJFileAdapter(this)