@file:OptIn(ExperimentalFoundationApi::class)

package com.niloda.aicontext.intellij.ui.queue

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.niloda.aicontext.intellij.ui.RoundedSurface
import com.niloda.aicontext.intellij.ui.Tooltip
import com.niloda.aicontext.intellij.ui.components.Col
import com.niloda.aicontext.intellij.ui.components.Row
import com.niloda.aicontext.intellij.ui.entry.Body2
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.delay

@Composable
fun FileRow(
    item: QueueItem,
    project: IProject,
    onRunClick: () -> Unit,
    onSaveClick: () -> Unit,
    onPromptChange: (String) -> Unit,
    onOutputDestChange: (String) -> Unit
) {
    var promptState: MutableState<String> = remember { mutableStateOf(item.prompt) }
    var outputDestState: MutableState<String> = remember { mutableStateOf(item.outputDestination) }
    var editingPrompt: MutableState<Boolean> = remember { mutableStateOf(false) }
    var editingOutputDestState: MutableState<Boolean> = remember { mutableStateOf(false) }

    // Local state to hold the displayed elapsed time
    val (elapsedTime, setElapsedTime) = remember { mutableStateOf(item.getElapsedTime()) }

    LaunchTimer(item, setElapsedTime)

    Row.VerticalCenter {
        Col.Max {
            Row.Max(content = {
                Tooltip({
                    RoundedSurface {
                        Body2(item.getDisplayPath(project))
                    }
                }) {
                    Row {
                        Body2(item.file.name)
                        Body2(item.status.toString())
                        Body2(elapsedTime)
                    }
                }
            })
        }
    }
}

@Composable
fun LaunchTimer(item: QueueItem, setElapsedTime: (String)->Unit) {
    LaunchedEffect(item.status) {
        if (item.status == QueueItem.Status.RUNNING) {
            while (true) {
                setElapsedTime(item.getElapsedTime())
                delay(1000) // Update every second
                if (item.status != QueueItem.Status.RUNNING) break
            }
        } else {
            setElapsedTime(item.getElapsedTime())
        }
    }
}

