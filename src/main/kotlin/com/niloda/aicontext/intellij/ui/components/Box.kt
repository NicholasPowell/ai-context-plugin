package com.niloda.aicontext.intellij.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder

object Box {
    @Composable
    inline fun Max(
        modifier: Modifier = Modifier,
        content: @Composable BoxScope.()-> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .debugBorder()
                .background(MaterialTheme.colors.surface)
                .then(modifier)
        ) {
            content()
        }
    }
}