package com.niloda.aicontext.intellij

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

fun LazyListScope.Group(
    groupName: String,
    headerBackground: Color,
    items: List<QueueItem>,
    textColor: Color,
    project: IProject
) {
    item(key = "header_$groupName") {
        val isExpandedState = remember { mutableStateOf(true) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBackground) // Slightly lighter background for headers
                .clickable { isExpandedState.value = !isExpandedState.value }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Group: $groupName (${items.size})",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f),
                color = textColor // Light gray text
            )
            Text(
                text = if (isExpandedState.value) "▼" else "▶",
                modifier = Modifier.padding(end = 8.dp),
                color = textColor // Light gray text
            )
        }

//        if (isExpandedState.value) {
        QueueTreeCells(scope = this@Group, queueItems = items, project = project)
//        }
    }
}

