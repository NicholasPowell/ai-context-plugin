package com.niloda.aicontext.intellij

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.niloda.aicontext.intellij.adapt.IntelliJFileAdapter
import com.niloda.aicontext.intellij.adapt.IntelliJProjectAdapter
import com.niloda.aicontext.model.AiFileProcessor
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.ollama.AiSender
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.mutableMapOf

object IntelliJAiFileProcessor : AiFileProcessor {
    override val queue = ConcurrentLinkedQueue<QueueItem>()
    private val activeTasks = mutableMapOf<IFile, Pair<Task.Backgroundable, ProgressIndicator>>()
    private val aiSender: AiSender = AiSender()

    override fun sendToAi(prompt: String, project: IProject): String? =
        aiSender.sendToAi(prompt, project)

    override fun enqueueFile(file: IFile) {
        EnqueueFile(file)
    }

    fun enqueueFileWithGroup(file: IFile, groupName: String) {
        val existingItem = queue.find { it.file == file }
        if (existingItem != null) {
            queue.remove(existingItem)
            if (existingItem.status == QueueItem.Status.RUNNING) {
                terminate(file)
            }
        }
        val item = QueueItem(file, groupName = groupName)
        queue.add(item)
        println("Queued file: ${file.name} in group: $groupName, Queue size: ${queue.size}")
        AiProcessorToolWindow.addToQueue(item, (file as? IntelliJFileAdapter)?.psiFile?.project?.adapt() ?: return)
    }

    override fun processFile(item: QueueItem, project: IProject) {
        ProcessFile(activeTasks, item, project)
    }

    override fun terminate(file: IFile) {
        Terminate(activeTasks, file)
    }

    override fun getQueueStatus(): List<QueueItem> = queue.toList()

    fun moveItemToGroup(item: QueueItem, newGroupName: String) {
        item.groupName = newGroupName
        println("Moved ${item.file.name} to group: $newGroupName")
    }
}

fun Project.adapt() = IntelliJProjectAdapter(this)
fun PsiFile.adapt() = IntelliJFileAdapter(this)