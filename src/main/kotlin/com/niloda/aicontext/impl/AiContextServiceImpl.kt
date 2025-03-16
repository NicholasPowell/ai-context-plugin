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
import com.niloda.aicontext.AiContextToolWindow
import com.intellij.util.ui.UIUtil
import com.niloda.aicontext.model.QueueItem

object AiContextServiceImpl : AiContextService {

    override val queue = ConcurrentLinkedQueue<QueueItem>()
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

    override fun processPromptForFile(project: IProject, file: IFile?): String =
        file?.text ?: ""

    override fun sendToAi(prompt: String, project: IProject): String? =
        aiSender.sendToAi(prompt, project)

    override fun queueFile(file: IFile) {
        val existingItem = queue.find { it.file == file }
        if (existingItem != null) {
            queue.remove(existingItem)
            if (existingItem.status == QueueItem.Status.RUNNING) {
                terminate(file)
            }
        }
        val item = QueueItem(file)
        queue.add(item)
        println("Queued file: ${file.name}, Queue size: ${queue.size}")
        AiContextToolWindow.addToQueue(item, (file as? IntelliJFileAdapter)?.psiFile?.project?.adapt() ?: return)
    }

    override fun processFile(item: QueueItem, project: IProject) {
        if (item.status != QueueItem.Status.PENDING) return
        item.status = QueueItem.Status.RUNNING
        item.startTime = System.currentTimeMillis()
        println("Processing file: ${item.file.name}")

        val task = object :
            Task.Backgroundable((project as IntelliJProjectAdapter).project, "Processing ${item.file.name}", true) {
            override fun run(indicator: ProgressIndicator) {
                if (indicator.isCanceled) {
                    println("Task for ${item.file.name} detected cancellation before starting")
                    handleCancellation(item, project)
                    return
                }

                val prompt = item.prompt + (item.file.text ?: "")
                val response = if (!indicator.isCanceled) sendToAi(prompt, project) else null
                if (indicator.isCanceled) {
                    println("Task for ${item.file.name} cancelled during execution")
                    handleCancellation(item, project)
                    return
                }

                item.status = if (response != null) QueueItem.Status.DONE else QueueItem.Status.ERROR
                item.startTime = null
                activeTasks.remove(item.file)
                println("Processing complete for ${item.file.name}, status: ${item.status}, response: ${response?.take(50) ?: "null"}")

                UIUtil.invokeLaterIfNeeded {
                    AiContextToolWindow.setResult(item, project, response)
                }
            }

            private fun handleCancellation(item: QueueItem, project: IProject) {
                item.status = QueueItem.Status.CANCELLED
                item.startTime = null
                activeTasks.remove(item.file)
                UIUtil.invokeLaterIfNeeded {
                    AiContextToolWindow.updateQueue(project)
                }
            }

            override fun onCancel() {
                println("onCancel triggered for ${item.file.name}")
                handleCancellation(item, project)
            }
        }
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, ProgressIndicatorBase().apply {
            activeTasks[item.file] = task to this
            println("Task started for ${item.file.name}")
        })
    }

    override fun terminate(file: IFile) {
        val (task, indicator) = activeTasks[file] ?: return
        println("Terminating task for ${file.name}")
        indicator.cancel()
        activeTasks.remove(file)
        val item = queue.find { it.file == file }
        if (item != null && item.status == QueueItem.Status.RUNNING) {
            item.status = QueueItem.Status.CANCELLED
            item.startTime = null
            UIUtil.invokeLaterIfNeeded {
                AiContextToolWindow.updateQueue((task.project as Project).adapt())
            }
        }
    }

    override fun getQueueStatus(): List<QueueItem> = queue.toList()
}

fun psiFileAdapter(project: Project, virtualFile: com.intellij.openapi.vfs.VirtualFile): PsiFile {
    val project = FileEditorManager.getInstance(project).project
    return com.intellij.psi.PsiManager.getInstance(project).findFile(virtualFile)!!
}

fun Project.adapt() = AiContextServiceImpl.IntelliJProjectAdapter(this)
fun PsiFile.adapt() = IntelliJFileAdapter(this)