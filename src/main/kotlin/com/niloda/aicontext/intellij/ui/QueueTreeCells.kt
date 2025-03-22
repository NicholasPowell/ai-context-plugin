package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import com.niloda.aicontext.intellij.uibridge.AiProcessorToolWindow
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.QueueManager

fun QueueTreeCells(
    scope: LazyListScope,
    queueItems: List<QueueItem>,
    project: IProject
) {
    scope.items(
        items = queueItems,
        key = { item -> item.file.virtualFilePath + item.status }
    ) { item ->
        QueueTreeCell(
            item = item,
            project = project,
            isSelected = false,
            onRunClick = { QueueManager.processFile(item, project) },
            onSaveClick = {
                AiProcessorToolWindow.saveResult(item, project)
            },
            onPromptChange = { /* Handled in QueueTreeCell */ },
            onOutputDestChange = { /* Handled in QueueTreeCell */ }
        )
    }
}