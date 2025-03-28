package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.niloda.aicontext.intellij.ui.theme.DarculaTheme
import com.niloda.aicontext.model.SendToAi
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AiProcessorComposeUI(
    queueState: StateFlow<List<QueueItem>>,
    project: IProject,
    sendToAi: SendToAi
) {
    DarculaTheme { // Wrap with the custom theme
        val queueItems = queueState.collectAsState().value
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background) // Use theme background
        ) {
            if (queueItems.isEmpty())
                NoItemsInQueue()
            else
                ScrollableGroups(queueState, project, sendToAi)
        }
    }
}