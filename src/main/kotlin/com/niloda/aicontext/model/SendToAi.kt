package com.niloda.aicontext.model

import androidx.compose.runtime.Composable
import com.niloda.aicontext.intellij.uibridge.Facade
import com.niloda.aicontext.model.QueueItem.Status
import com.niloda.aicontext.ollama.SendToOllama
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface AiSender {
    operator fun invoke(item: QueueItem, project: IProject)
}


class BackgroundSender(val sendToOllama: SendToOllama): AiSender {
    override operator fun invoke(item: QueueItem, project: IProject) {
        CoroutineScope(Dispatchers.IO).launch {
            Facade.dataStore.add(item.copy(status = Status.RUNNING))
            val result = sendToOllama(item.prompt + (item.file.text ?: ""), project)
            Facade.dataStore.add(item.copy(status = Status.DONE, result = result))
        }
    }
}