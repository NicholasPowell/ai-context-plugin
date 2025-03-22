package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.niloda.aicontext.model.QueueManager
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

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

    PeriodicallyUpdateQueueItemsAndCheckRunningTasks(queueItems, queueItemsState, runningTasksState)
    ForceRecompositionForRunningTasks(hasRunningTasks, runningTasksState, queueItemsState)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // Set panel background to Darcula dark gray
    ) {
        if (queueItems.isEmpty())
            NoItemsInQueue(textColor)
        else
            ScrollableGroups(groupedItems, headerBackground, textColor, project)
    }
}

