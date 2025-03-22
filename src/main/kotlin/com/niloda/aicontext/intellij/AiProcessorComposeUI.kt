package com.niloda.aicontext.intellij

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.delay

@Composable
fun AiProcessorComposeUI(project: IProject) {
    // IntelliJ Darcula-inspired colors
    val backgroundColor = Color(0xFF2B2B2B) // Dark gray background (Darcula)
    val textColor = Color(0xFFA9B7C6) // Light gray text (Darcula)
    val headerBackground = Color(0xFF3C3F41) // Slightly lighter gray for headers (Darcula)

    // State to track queue items
    val queueItemsState = remember { mutableStateOf(QueueManager.aiService.queue.toList()) }
    val queueItems by queueItemsState
    val groupedItems by derivedStateOf { queueItems.groupBy { it.groupName } }

    // State to track if there are running tasks
    var hasRunningTasks by remember { mutableStateOf(queueItems.any { it.status == QueueItem.Status.RUNNING }) }

    // Periodically update the queue items and check for running tasks
    LaunchedEffect(Unit) {
        while (true) {
            val newQueueItems = QueueManager.aiService.queue.toList()
            if (newQueueItems != queueItems) {
                println("Queue updated: ${newQueueItems.size} items")
                queueItemsState.value = newQueueItems
            }
            hasRunningTasks = newQueueItems.any { it.status == QueueItem.Status.RUNNING }
            delay(1000)
        }
    }

    // Separate LaunchedEffect to force recomposition for running tasks
    LaunchedEffect(hasRunningTasks) {
        if (hasRunningTasks) {
            while (true) {
                queueItemsState.value = queueItemsState.value.toList() // Force recomposition
                delay(1000)
                if (!queueItemsState.value.any { it.status == QueueItem.Status.RUNNING }) {
                    hasRunningTasks = false
                    break
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // Set panel background to Darcula dark gray
    ) {
        if (queueItems.isEmpty()) {
            Text(
                text = "No items in queue. Enqueue files to see them here.",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.body1,
                color = textColor // Light gray text
            )
        } else {
            val scrollState = rememberScrollState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                state = rememberLazyListState()
            ) {
                groupedItems.forEach { (groupName, items) ->
                    item(key = "header_$groupName") {
                        val isExpandedState = remember { mutableStateOf(true) }
                        val isExpanded by isExpandedState

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
                                text = if (isExpanded) "▼" else "▶",
                                modifier = Modifier.padding(end = 8.dp),
                                color = textColor // Light gray text
                            )
                        }

                        if (isExpanded) {
                            this@LazyColumn.items(
                                items = items,
                                key = { item -> item.file.virtualFilePath + item.status }
                            ) { item ->
                                QueueTreeCell(
                                    item = item,
                                    project = project,
                                    isSelected = false,
                                    onRunClick = {
                                        QueueManager.processFile(item, project)
                                        AiProcessorToolWindow.startTimer()
                                    },
                                    onSaveClick = {
                                        AiProcessorToolWindow.saveResult(item, project)
                                    },
                                    onPromptChange = { /* Handled in QueueTreeCell */ },
                                    onOutputDestChange = { /* Handled in QueueTreeCell */ }
                                )
                            }
                        }
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}