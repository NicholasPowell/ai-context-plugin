package com.niloda.aicontext.model

import com.niloda.aicontext.intellij.uibridge.Facade.Companion.facade
import com.niloda.aicontext.ollama.SendToOllama
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackgroundSendToOllama(val sendToOllama: SendToOllama): SendToAi {
    override operator fun invoke(item: QueueItem, project: IProject) {
        CoroutineScope(Dispatchers.IO).launch {
            project.facade.dataStore.add(item.copy(status = QueueItem.Status.RUNNING))
            val result = sendToOllama(item.prompt + (item.file.text ?: ""), project)
            project.facade.dataStore.add(item.copy(status = QueueItem.Status.DONE, result = result))
        }
    }
}