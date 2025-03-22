package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.graphics.Color
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

fun LazyGroupedItems(
    scope: LazyListScope,
    groupedItems: Map<String, List<QueueItem>>,
    headerBackground: Color,
    textColor: Color,
    project: IProject
) {
    groupedItems.forEach { (groupName, items) ->
        Group(scope, groupName, headerBackground, items, textColor, project)
    }
}