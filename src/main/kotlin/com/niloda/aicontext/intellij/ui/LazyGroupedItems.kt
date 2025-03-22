// File: src/main/kotlin/com/niloda/aicontext/intellij/ui/LazyGroupedItems.kt
package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.lazy.LazyListScope
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

fun LazyGroupedItems(
    scope: LazyListScope,
    groupedItems: Map<String, List<QueueItem>>,
    project: IProject
) {
    groupedItems.forEach { (groupName, items) ->
        Group(scope, groupName, items, project)
    }
}