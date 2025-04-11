package com.niloda.aicontext.intellij.ui.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import com.niloda.aicontext.intellij.ui.components.Box
import com.niloda.aicontext.intellij.ui.entry.Body1
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.flow.StateFlow

@Composable
inline fun DetailComposeUI(
    currentRow: StateFlow<QueueItem?>,
    theme: @Composable (@Composable () -> Unit
) -> Unit) {
    val row = currentRow.collectAsState()
    theme{
        Box.Max {
            Body1("Hello $row")
        }
    }
}