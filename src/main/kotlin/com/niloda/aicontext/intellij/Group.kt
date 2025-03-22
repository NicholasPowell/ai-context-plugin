// === File: src/main/kotlin/com/niloda/aicontext/intellij/Group.kt
package com.niloda.aicontext.intellij

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

fun Group(
    scope: LazyListScope,
    groupName: String,
    headerBackground: Color,
    items: List<QueueItem>,
    textColor: Color,
    project: IProject
) {
    println("Rendering group: $groupName with ${items.size} items") // Debug log
    scope.item(key = "header_$groupName") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBackground)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Group: $groupName (${items.size})",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f),
                color = textColor
            )
        }
        println("Group $groupName expanded, rendering ${items.size} items") // Debug log
        QueueTreeCells(scope = scope, queueItems = items, project = project)
    }
}