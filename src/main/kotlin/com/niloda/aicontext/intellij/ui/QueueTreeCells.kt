package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.SendToAi

@Composable
fun QueueTreeCells(
    queueItems: List<QueueItem>,
    project: IProject,
    sendToAi: SendToAi
) {
    Column {
        queueItems.forEach { item ->
            QueueTreeCell(
                item = item,
                project = project,
                isSelected = false,
                onRunClick = { sendToAi.processFile(item, project) },
                onSaveClick = {  },
                onPromptChange = { /* Handled in QueueTreeCell */ },
                onOutputDestChange = { /* Handled in QueueTreeCell */ }
            )
        }
    }
}