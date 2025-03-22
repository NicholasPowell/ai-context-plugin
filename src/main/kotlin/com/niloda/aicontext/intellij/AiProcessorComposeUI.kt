package com.niloda.aicontext.intellij

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
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

    val runningTasksState = remember { mutableStateOf(queueItems.any { it.status == QueueItem.Status.RUNNING }) }
    // State to track if there are running tasks
    var hasRunningTasks by runningTasksState

//    PeriodicallyUpdateQueueItemsAndCheckRunningTasks(queueItems, queueItemsState, runningTasksState)
//    ForceRecompositionForRunningTasks(hasRunningTasks, runningTasksState, queueItemsState)
    LaunchedEffect(Unit) {
        while (true) {
            val newQueueItems = QueueManager.aiService.queue.toList()
            if (newQueueItems != queueItems) {
                println("Queue updated: ${newQueueItems.size} items")
                queueItemsState.value = newQueueItems
            }
            runningTasksState.value = newQueueItems.any { it.status == QueueItem.Status.RUNNING }
            delay(1000)
        }
    }
    LaunchedEffect(hasRunningTasks) {
        if (runningTasksState.value) {
            while (true) {
                queueItemsState.value = queueItemsState.value.toList() // Force recomposition
                delay(1000)
                if (!queueItemsState.value.any { it.status == QueueItem.Status.RUNNING }) {
                    runningTasksState.value = false
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
            NoItemsInQueue(textColor)
        } else {
            val scrollState = rememberScrollState()
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                state = rememberLazyListState()
            ) {
                groupedItems.forEach { (groupName, items) ->
                    Group(groupName, headerBackground, items, textColor, project)
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}
