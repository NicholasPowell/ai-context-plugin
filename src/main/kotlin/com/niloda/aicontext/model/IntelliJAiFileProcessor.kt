package com.niloda.aicontext.model

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.niloda.aicontext.intellij.uibridge.AiProcessorToolWindow
import com.niloda.aicontext.ollama.AiSender

object IntelliJAiFileProcessor : AiFileProcessor {
    private val activeTasks = mutableMapOf<IFile, Pair<Task.Backgroundable, ProgressIndicator>>()
    private val aiSender: AiSender = AiSender()

    override fun sendToAi(prompt: String, project: IProject): String? =
        aiSender.sendToAi(prompt, project)

    override fun enqueueFile(file: IFile) {
        EnqueueFile(file)
    }

    fun enqueueFileWithGroup(file: IFile, groupName: String) {
        val existingItem = AiProcessorToolWindow._queueFlow.value.find { it.file == file }
        if (existingItem != null) {
            AiProcessorToolWindow._queueFlow.value -= existingItem
            if (existingItem.status == QueueItem.Status.RUNNING) {
                terminate(file)
            }
        }
        val item = QueueItem(file, groupName = groupName)
        AiProcessorToolWindow._queueFlow.value += item
        println("Queued file: ${file.name} in group: $groupName, Queue size: ${AiProcessorToolWindow._queueFlow.value.size}")
    }

    override fun processFile(item: QueueItem, project: IProject) {
        ProcessFile(activeTasks, item, project)
    }

    override fun terminate(file: IFile) {
        Terminate(activeTasks, file)
    }

    override fun getQueueStatus(): List<QueueItem> = AiProcessorToolWindow._queueFlow.value.toList()


}