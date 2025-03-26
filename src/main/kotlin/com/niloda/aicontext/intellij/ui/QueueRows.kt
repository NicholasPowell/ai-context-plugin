package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.niloda.aicontext.intellij.uibridge.DataStore
import com.niloda.aicontext.intellij.uibridge.Facade
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.QueueItem.Status
import com.niloda.aicontext.model.SendToAi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun QueueRows(
    queueItems: List<QueueItem>,
    project: IProject,
    sendToAi: SendToAi
) {
    Column {
        queueItems.forEach { item ->
            QueueItemRow(
                item = item,
                project = project,
                onRunClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        Facade.dataStore.add(item.copy(status = Status.RUNNING))
                        sendToAi(item, project)
                        Facade.dataStore.add(item.copy(status = Status.DONE))
                    }
                 },
                onSaveClick = {  },
                onPromptChange = { /* Handled in QueueTreeCell */ },
                onOutputDestChange = { /* Handled in QueueTreeCell */ }
            )
        }
    }
}