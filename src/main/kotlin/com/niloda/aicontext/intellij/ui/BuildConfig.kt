package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object BuildConfig {
    const val DEBUG = false
    val colors = listOf(
        Color.Companion.Magenta,
        Color.Companion.Yellow,
        Color.Companion.Cyan,
        Color.Companion.White,
        Color.Companion.Blue,
        Color.Companion.Green
    )
    var iterator = colors.iterator()
    fun nextColor(): Color {
        if (!iterator.hasNext())
            iterator = colors.iterator()
        return iterator.next()
    }

    fun Modifier.debugBorder(color: Color? = null) = then(
        when {
            DEBUG -> Modifier.Companion.border(1.dp, nextColor())
            color != null -> Modifier.Companion.border(2.dp, color)
            else -> Modifier.Companion
        }
    )
    fun debugBorder(color: Color? = null): Modifier = Modifier.Companion.debugBorder(color)
}