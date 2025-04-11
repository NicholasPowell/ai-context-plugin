package com.niloda.aicontext.model

import com.niloda.aicontext.intellij.uibridge.QueueFacade.Companion.queueFacade
import com.niloda.aicontext.ollama.SendToOllama
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackgroundSendToOllama(val sendToOllama: SendToOllama): SendToAi {
    override operator fun invoke(item: QueueItem, project: IProject) {
        CoroutineScope(Dispatchers.IO).launch {
            project.queueFacade.queueDataStore.add(item.copy(status = QueueItem.Status.RUNNING))
            val result = sendToOllama(item.prompt + (item.file.text ?: ""), project)
            project.queueFacade.queueDataStore.add(item.copy(status = QueueItem.Status.DONE, result = result))
        }
    }
}