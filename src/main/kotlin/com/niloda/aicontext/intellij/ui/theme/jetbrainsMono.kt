package com.niloda.aicontext.intellij.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

val jetbrainsMono = FontFamily(
    Font(
        resource = "fonts/variable/JetBrainsMono[wght].ttf",
        weight = FontWeight.Companion.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400) // Default weight value
        )
    )
)