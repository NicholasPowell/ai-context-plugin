package com.niloda.aicontext.intellij.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarculaColors = darkColors(
    primary = Color(0xFF1A4B7D), // Blue selection highlight (Darcula)
    onPrimary = Color(0xFFA9B7C6), // Light gray text
    background = Color(0xFF2B2B2B), // Dark gray background
    surface = Color(0xFF3C3F41), // Slightly lighter gray for headers/editing
    onSurface = Color(0xFFA9B7C6), // Light gray text
    secondary = Color(0xFF555555), // Darker gray for borders
    onBackground = Color(0xFFA9B7C6) // Light gray text for background
)

@Composable
fun DarculaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarculaColors,
        content = content
    )
}