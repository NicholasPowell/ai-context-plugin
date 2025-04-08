package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.intellij.ui.dsl.builder.Align
import com.niloda.aicontext.intellij.ui.components.Box
import com.niloda.aicontext.intellij.ui.components.Col
import com.niloda.aicontext.intellij.ui.components.Row
import org.jetbrains.jewel.ui.component.HorizontalScrollbar
import org.jetbrains.jewel.ui.component.VerticalScrollbar

@Composable
fun BoxScroll(
    contents: @Composable ()->Unit
) {
    val scrollState = rememberScrollState()
    val scrollStateH = rememberScrollState()
    val scrollbarStyle = LocalScrollbarStyle.current

    Box.Max {
        Col.Wide(modifier = Modifier.verticalScroll(scrollState).align(Alignment.TopEnd)) {
            Row.Wide(modifier = Modifier.horizontalScroll(scrollStateH)) {
                Col.Wide {
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
                    .padding(top = scrollbarStyle.thickness)
        )
        VerticalScrollbar(
            scrollState = scrollState,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight()
        )
    }
}