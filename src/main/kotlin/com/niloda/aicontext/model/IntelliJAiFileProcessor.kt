package com.niloda.aicontext.model

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.niloda.aicontext.intellij.uibridge.AiProcessorToolWindow
import com.niloda.aicontext.intellij.uibridge.DataStore
import com.niloda.aicontext.intellij.uibridge.Facade
import com.niloda.aicontext.ollama.AiSender

class IntelliJAiFileProcessor(
    private val dataStore: DataStore
) : AiFileProcessor {
    private val activeTasks = mutableMapOf<IFile, Pair<Task.Backgroundable, ProgressIndicator>>()
    private val aiSender: AiSender = AiSender()

    override fun sendToAi(prompt: String, project: IProject): String? =
        aiSender.sendToAi(prompt, project)

    override fun enqueueFile(file: IFile) {
        EnqueueFile(file)
    }

    fun enqueueFileWithGroup(file: IFile) {
        val existingItem = Facade.dataStore._queueFlow.value.find { it.file == file }
        if (existingItem != null) {
            dataStore._queueFlow.value -= existingItem
            if (existingItem.status == QueueItem.Status.RUNNING) {
                terminate(file)
            }
        }
        val item = QueueItem(file, groupName = "Default")
        dataStore._queueFlow.value += item
        println("Queued file: ${file.name} in group: Default, Queue size: ${dataStore._queueFlow.value
            .size}")
    }

    override fun processFile(item: QueueItem, project: IProject) {
        ProcessFile(activeTasks, item, project)
    }

    override fun terminate(file: IFile) {
        Terminate(activeTasks, file)
    }

    override fun getQueueStatus(): List<QueueItem> = dataStore._queueFlow.value.toList()

}