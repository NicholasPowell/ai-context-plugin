package com.niloda.aicontext.intellij.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.delay

@Composable
fun ForceRecompositionForRunningTasks(
    hasRunningTasks: Boolean,
    runningTasksState: MutableState<Boolean>,
    queueItemsState: MutableState<List<QueueItem>>
) {
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
}