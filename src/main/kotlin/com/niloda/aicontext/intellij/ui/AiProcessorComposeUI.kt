// File: src/main/kotlin/com/niloda/aicontext/intellij/ui/AiProcessorComposeUI.kt
package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.niloda.aicontext.model.QueueManager
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

@Composable
fun AiProcessorComposeUI(project: IProject) {
    // State to track queue items
    val queueItemsState = remember { mutableStateOf(QueueManager.aiService.queue.toList()) }
    val queueItems by queueItemsState
    val groupedItems by derivedStateOf { queueItems.groupBy { it.groupName } }

    val runningTasksState = remember { mutableStateOf(queueItems.any { it.status == QueueItem.Status.RUNNING }) }
    var hasRunningTasks by runningTasksState

    PeriodicallyUpdateQueueItemsAndCheckRunningTasks(queueItems, queueItemsState, runningTasksState)
    ForceRecompositionForRunningTasks(hasRunningTasks, runningTasksState, queueItemsState)

    AiContextTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            if (queueItems.isEmpty())
                NoItemsInQueue()
            else
                ScrollableGroups(groupedItems, project)
        }
    }
}