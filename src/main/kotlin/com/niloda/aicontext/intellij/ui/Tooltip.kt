@file:OptIn(ExperimentalFoundationApi::class)
package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.runtime.Composable


@Composable
fun Tooltip(tooltip: @Composable () -> Unit, content: @Composable () -> Unit) {
    TooltipArea(
        tooltip = tooltip,
        modifier = BuildConfig.debugBorder(),
        delayMillis = 500,
        content = content
    )
}