package com.niloda.aicontext.intellij.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder

@Composable
inline fun FullWidthColumn(modifier: Modifier = Modifier, content: @Composable ()->Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .debugBorder()
                .background(MaterialTheme.colors.surface)
                .then(modifier),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        content()
    }
}
@Composable
inline fun FullWidthRow(modifier: Modifier = Modifier, content: @Composable ()->Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .debugBorder()
                .background(MaterialTheme.colors.surface)
                .then(modifier)
    ) {
        content()
    }
}
object R {
    @Composable
    inline fun Wide(
        modifier: Modifier = Modifier,
        content: @Composable ()->Unit) = FullWidthRow(modifier) { content() }
}

object Col {
    @Composable
    inline fun Wide(
        modifier: Modifier = Modifier,
        content: @Composable ()->Unit
    ) = FullWidthColumn(modifier) { content() }


    @Composable
    inline operator fun invoke(modifier: Modifier = Modifier, content: @Composable ()->Unit) =
        Column(modifier.debugBorder()) { content() }
}
