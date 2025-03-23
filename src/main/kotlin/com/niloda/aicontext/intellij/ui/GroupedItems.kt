package com.niloda.aicontext.intellij.ui

import androidx.compose.runtime.Composable
import com.niloda.aicontext.intellij.ui.entry.Group
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.SendToAi

@Composable
fun GroupedItems(
    groupedItems: Map<String, List<QueueItem>>,
    project: IProject,
    sendToAi: SendToAi
) {
    groupedItems.forEach { (groupName, items) ->
        Group(
            groupName = groupName,
            items = items,
            project = project,
            sendToAi = sendToAi
        )
    }
}