package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niloda.aicontext.QueueUIConstants
import com.niloda.aicontext.model.QueueItem

@Composable
fun Prompt(
    item: QueueItem,
    editingPrompt: MutableState<Boolean>,
    promptState: MutableState<String>,
    onPromptChange: (String) -> Unit
) {
    var isEditingPrompt by editingPrompt
    var prompt by promptState
    Box(
        modifier = Modifier
            .width(QueueUIConstants.PROMPT_WIDTH.dp)
            .padding(end = QueueUIConstants.INSET.dp)
            .border(if (isEditingPrompt) 1.dp else 0.dp, MaterialTheme.colors.secondary) // Use theme border color
            .background(if (isEditingPrompt) MaterialTheme.colors.surface else MaterialTheme.colors.background) // Use theme colors
            .clickable { isEditingPrompt = true }
    ) {
        if (isEditingPrompt) {
            BasicTextField(
                value = prompt,
                onValueChange = {
                    prompt = it
                    onPromptChange(it)
                    item.prompt = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colors.onSurface) // Use theme text color
            )
        } else {
            Text(
                text = prompt,
                modifier = Modifier.padding(2.dp),
                style = TextStyle(fontSize = 14.sp),
                color = MaterialTheme.colors.onSurface, // Use theme text color
                maxLines = 1
            )
        }
    }
}