package com.niloda.aicontext.intellij.ui.entry

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder

@Composable
fun Body1(
    text: String,
    modifier: Modifier = Modifier.Companion,
    color: Color = MaterialTheme.colors.onSurface
) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1,
        modifier = Modifier.Companion.fillMaxWidth().debugBorder().then(modifier),
        color = color
    )
}

@Composable
fun Body2(
    text: String,
    modifier: Modifier = Modifier.Companion,
    color: Color = MaterialTheme.colors.onSurface
) {
    Text(
        text = text,
        style = MaterialTheme.typography.body2,
        modifier = Modifier.Companion.fillMaxWidth().debugBorder().then(modifier),
        color = color
    )
}

