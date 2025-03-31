package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder

@Composable
fun TooltipSurface(content: @Composable ()-> Unit) {
    Surface(
        modifier = Modifier.Companion.debugBorder(),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colors.surface,
        elevation = 4.dp,
        content = content
    )
}