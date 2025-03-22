package com.niloda.aicontext.intellij

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

}