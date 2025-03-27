package com.niloda.aicontext.intellij.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ToggleIcon(
    state: Pair<Boolean, (Boolean) -> Unit>,
    onOff: Pair<ImageVector, ImageVector>,
    size: Dp = 20.dp,
    modifier: Modifier = Modifier.Companion,
    iconModifier: Modifier = Modifier.Companion,
    onDescription: String = "On",
    offDescription: String = "Off",
) {
    val (isOn, setToggle) = state
    val (on, off) = onOff
    IconButton(
        onClick = { setToggle(!isOn) },
        modifier = modifier.then(Modifier.size(size)),
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