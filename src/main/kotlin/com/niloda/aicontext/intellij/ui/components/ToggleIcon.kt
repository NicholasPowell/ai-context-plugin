package com.niloda.aicontext.intellij.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ToggleIcon(
    toggleState: Pair<Boolean, (Boolean) -> Unit>,
    onOff: Pair<ImageVector, ImageVector>,
    modifier: Modifier = Modifier.Companion,
    iconModifier: Modifier = Modifier.Companion,
    onDescription: String = "On",
    offDescription: String = "Off"
) {
    val (isOn, setToggle) = toggleState
    val (on, off) = onOff
    IconButton(
        onClick = { setToggle(!isOn) },
        modifier = modifier,
        enabled = true
    ) {
        Icon(
            imageVector = if (isOn) on else off,
            contentDescription = if (isOn) onDescription else offDescription,
            tint = MaterialTheme.colors.onSurface,
            modifier = iconModifier
        )
    }
}