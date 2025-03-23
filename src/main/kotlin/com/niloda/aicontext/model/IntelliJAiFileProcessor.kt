package com.niloda.aicontext.model

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.niloda.aicontext.intellij.uibridge.Facade
import com.niloda.aicontext.ollama.AiSender

class IntelliJAiFileProcessor() : AiFileProcessor {
    private val activeTasks = mutableMapOf<IFile, Pair<Task.Backgroundable, ProgressIndicator>>()
    private val aiSender: AiSender = AiSender()

    override fun sendToAi(prompt: String, project: IProject): String? =
        aiSender.sendToAi(prompt, project)

    override fun enqueueFile(file: IFile) {
        EnqueueFile(file)
    }

    fun enqueueFileWithGroup(file: IFile) {
        val existingItem = Facade.dataStore.find(file)
        if (existingItem != null) {
            Facade.dataStore.remove(existingItem)
            if (existingItem.status == QueueItem.Status.RUNNING) {
                terminate(file)
            }
        }
        val item = QueueItem(file, groupName = "Default")
        Facade.dataStore.add(item)
    }

    override fun processFile(item: QueueItem, project: IProject) {
        ProcessFile(activeTasks, item, project)
    }

    override fun terminate(file: IFile) {
        Terminate(activeTasks, file)
    }


}