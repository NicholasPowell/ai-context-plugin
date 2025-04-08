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
import androidx.compose.ui.platform.testTag
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder

@Composable
inline fun FullWidthColumn(tag: String, modifier: Modifier = Modifier, content: @Composable ()->Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .debugBorder()
                .testTag(tag)
                .background(MaterialTheme.colors.surface)
                .then(modifier),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        content()
    }
}
@Composable
inline fun FullWidthRow(tag: String, modifier: Modifier = Modifier, content: @Composable ()->Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .debugBorder()
                .testTag(tag)
                .background(MaterialTheme.colors.surface)
                .then(modifier)
    ) {
        content()
    }
}
object Row {
    @Composable
    inline fun Max(
        tag: String = "",
        modifier: Modifier = Modifier,
        content: @Composable ()->Unit) = FullWidthRow(tag, modifier) { content() }

    @Composable
    inline operator fun invoke(
        modifier: Modifier = Modifier,
        valign: Alignment.Vertical = Alignment.Top,
        content: @Composable ()
    ->Unit) =
        Row(
            modifier = modifier.debugBorder(),
            verticalAlignment = valign
        ) { content() }

    @Composable
    inline fun VerticalCenter(
        modifier: Modifier = Modifier,
        content: @Composable ()->Unit) =
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content() }

}

object Col {
    @Composable
    inline fun Max(
        tag: String = "",
        modifier: Modifier = Modifier,
        content: @Composable ()->Unit
    ) {
        FullWidthColumn(tag, modifier) { content() }
    }


    @Composable
    inline operator fun invoke(modifier: Modifier = Modifier, content: @Composable ()->Unit) =
        Column(modifier.debugBorder()) { content() }
}

