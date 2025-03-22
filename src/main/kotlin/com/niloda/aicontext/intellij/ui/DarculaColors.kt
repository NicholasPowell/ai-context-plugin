// File: src/main/kotlin/com/niloda/aicontext/intellij/ui/AiContextTheme.kt
package com.niloda.aicontext.intellij.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarculaColors = darkColors(
    background = Color(0xFF2B2B2B), // Dark gray background (Darcula)
    surface = Color(0xFF3C3F41),    // Slightly lighter gray for headers/surfaces (Darcula)
    onBackground = Color(0xFFA9B7C6), // Light gray text (Darcula)
    onSurface = Color(0xFFA9B7C6),  // Light gray text for surfaces
    primary = Color(0xFF1A4B7D),    // Blue selection highlight (Darcula)
    secondary = Color(0xFF555555),  // Darker gray border (Darcula)
    // Additional colors for specific UI elements
    error = Color.Red
)

@Composable
fun AiContextTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarculaColors,
        content = content
    )
}