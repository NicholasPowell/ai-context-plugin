package com.niloda.aicontext.intellij.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.niloda.aicontext.intellij.ui.components.Box
import com.niloda.aicontext.intellij.ui.entry.Group
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.SendToAi
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
inline fun AiProcessorComposeUI(
    queueState: StateFlow<List<QueueItem>>,
    project: IProject,
    sendToAi: SendToAi,
    theme: @Composable (@Composable () -> Unit) -> Unit
) {
    val groupedItems = queueState.collectAsState().value.groupBy { it.groupName }

    theme { // Wrap with the custom theme
        val queueItems = queueState.collectAsState().value

            if (queueItems.isEmpty())
                Box.Max {
                    NoItemsInQueue()
                }
            else
                Box.Max {
                    BoxScroll {
                        groupedItems.forEach { (groupName, items) ->
                            Group(
                                groupName = groupName,
                                items = items,
                                project = project,
                                sendToAi = sendToAi
                            )
                        }
                    }

        }
    }
}