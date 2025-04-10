package com.niloda.aicontext.intellij.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.niloda.aicontext.intellij.ui.components.Box
import com.niloda.aicontext.intellij.ui.entry.Body1

@Composable
inline fun DetailComposeUI(
    currentRow: MutableState<String>,
    theme: @Composable (@Composable () -> Unit
) -> Unit) {
    theme{
        Box.Max {
            Body1("Hello $currentRow")
        }
    }
}