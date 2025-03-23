package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.niloda.aicontext.intellij.uibridge.AiProcessorToolWindow
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.QueueManager

@Composable
fun QueueTreeCells(
    queueItems: List<QueueItem>,
    project: IProject
) {
    Column {
        queueItems.forEach { item ->
            QueueTreeCell(
                item = item,
                project = project,
                isSelected = false,
                onRunClick = { QueueManager.processFile(item, project) },
                onSaveClick = { AiProcessorToolWindow.saveResult(item, project) },
                onPromptChange = { /* Handled in QueueTreeCell */ },
                onOutputDestChange = { /* Handled in QueueTreeCell */ }
            )
        }
    }
}