package com.niloda.aicontext.intellij

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.delay

@Composable
fun PeriodicallyUpdateQueueItemsAndCheckRunningTasks(
    queueItems: List<QueueItem>,
    queueItemsState: MutableState<List<QueueItem>>,
    runningTasksState: MutableState<Boolean>
) {
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
}