package com.niloda.aicontext.intellij.ui

import androidx.compose.runtime.Composable
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

@Composable
fun GroupedItems(
    groupedItems: Map<String, List<QueueItem>>,
    project: IProject
) {
    groupedItems.forEach { (groupName, items) ->
        Group(
            groupName = groupName,
            items = items,
            project = project
        )
    }
}