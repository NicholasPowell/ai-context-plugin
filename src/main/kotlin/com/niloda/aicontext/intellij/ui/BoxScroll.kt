package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.niloda.aicontext.intellij.ui.components.Box
import com.niloda.aicontext.intellij.ui.components.Col
import com.niloda.aicontext.intellij.ui.components.Row
import org.jetbrains.jewel.ui.component.HorizontalScrollbar
import org.jetbrains.jewel.ui.component.VerticalScrollbar

/**
 * Combines Vertical and Horizontal scroll components
 */
@Composable
fun BoxScroll(
    contents: @Composable ()->Unit
) {
    val scrollState = rememberScrollState()
    val scrollStateH = rememberScrollState()
    val scrollbarStyle = LocalScrollbarStyle.current

    Box.Max {
        Col.Max("", Modifier.verticalScroll(scrollState).align(Alignment.TopEnd)) {
            Row.Max("",Modifier.horizontalScroll(scrollStateH)) {
                Col.Max {
                    contents()
                }
            }
        }
        HorizontalScrollbar(
            scrollState = scrollStateH,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .padding(end = scrollbarStyle.thickness)

        )
        VerticalScrollbar(
            scrollState = scrollState,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight()
        )
    }
}