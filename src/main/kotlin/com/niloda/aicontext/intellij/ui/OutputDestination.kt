package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niloda.aicontext.QueueUIConstants
import com.niloda.aicontext.model.QueueItem

@Composable
fun OutputDestination(
    item: QueueItem,
    editingOutputDestState: MutableState<Boolean>,
    outputDestState: MutableState<String>,
    onOutputDestChange: (String) -> Unit,
    onRunClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var isEditingOutputDest by editingOutputDestState
    var outputDest by outputDestState
    Box(
        modifier = Modifier
            .width(QueueUIConstants.OUTPUT_DEST_WIDTH.dp)
            .padding(end = QueueUIConstants.INSET.dp)
            .border(if (isEditingOutputDest) 1.dp else 0.dp, MaterialTheme.colors.secondary) // Use theme border color
            .background(if (isEditingOutputDest) MaterialTheme.colors.surface else MaterialTheme.colors.background) // Use theme colors
            .clickable { isEditingOutputDest = true }
    ) {
        if (isEditingOutputDest) {
            BasicTextField(
                value = outputDest,
                onValueChange = {
                    outputDest = it
                    onOutputDestChange(it)
                    item.outputDestination = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colors.onSurface) // Use theme text color
            )
        } else {
            Text(
                text = outputDest,
                modifier = Modifier.padding(2.dp),
                style = TextStyle(fontSize = 14.sp),
                color = MaterialTheme.colors.onSurface, // Use theme text color
                maxLines = 1
            )
        }
    }

    // Status (non-editable)
    Text(
        text = item.status.toString(),
        modifier = Modifier
            .width(QueueUIConstants.STATUS_WIDTH.dp)
            .padding(end = QueueUIConstants.INSET.dp),
        style = TextStyle(fontSize = 14.sp),
        color = MaterialTheme.colors.onSurface // Use theme text color
    )

    // Run Button
    if (item.status == QueueItem.Status.PENDING) {
        IconButton(
            onClick = { onRunClick() },
            modifier = Modifier
                .size(20.dp)
                .pointerHoverIcon(PointerIcon.Hand)
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Run this item",
                tint = MaterialTheme.colors.onSurface // Use theme icon color
            )
        }
    } else {
        Spacer(modifier = Modifier.size(20.dp))
    }

    // Save Button
    if (item.result != null && item.outputDestination.isNotBlank()) {
        IconButton(
            onClick = { onSaveClick() },
            modifier = Modifier
                .size(20.dp)
                .pointerHoverIcon(PointerIcon.Hand)
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Save Result",
                tint = MaterialTheme.colors.onSurface // Use theme icon color
            )
        }
    }
}