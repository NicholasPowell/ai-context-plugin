package com.niloda.aicontext.intellij.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niloda.aicontext.QueueUIConstants
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IFileEditorManager
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.delay

@Composable
fun QueueTreeCell(
    item: QueueItem,
    project: IProject,
    isSelected: Boolean,
    onRunClick: () -> Unit,
    onSaveClick: () -> Unit,
    onPromptChange: (String) -> Unit,
    onOutputDestChange: (String) -> Unit
) {
    // IntelliJ Darcula-inspired colors
    val rowBackground = Color(0xFF2B2B2B) // Dark gray background (Darcula)
    val selectedBackground = Color(0xFF1A4B7D) // Blue selection highlight (Darcula)
    val textColor = Color(0xFFA9B7C6) // Light gray text (Darcula)

    var promptState: MutableState<String> = remember { mutableStateOf(item.prompt) }
    var outputDestState: MutableState<String> = remember { mutableStateOf(item.outputDestination) }
    var editingPrompt: MutableState<Boolean> = remember { mutableStateOf(false) }
    var editingOutputDestState: MutableState<Boolean> = remember { mutableStateOf(false) }
    var editingOutputDest by editingOutputDestState
    var outputDest by outputDestState

    // Local state to hold the displayed elapsed time
    var elapsedTime by remember { mutableStateOf(item.getElapsedTime()) }

    // Update elapsed time for running tasks
    LaunchedEffect(item.status) {
        if (item.status == QueueItem.Status.RUNNING) {
            while (true) {
                elapsedTime = item.getElapsedTime()
                delay(1000) // Update every second
                if (item.status != QueueItem.Status.RUNNING) break
            }
        } else {
            elapsedTime = item.getElapsedTime() // Ensure final state when not running
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) selectedBackground else rowBackground) // Selection or default background
            .padding(4.dp)
            .border(1.dp, Color.Red),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.getDisplayPath(project),
            modifier = Modifier
                .width(QueueUIConstants.FILE_PATH_WIDTH.dp)
                .padding(end = QueueUIConstants.INSET.dp),
            style = TextStyle(fontSize = 14.sp),
            maxLines = 1,
            color = textColor // Light gray text
        )
        Prompt(
            item = item,
            editingPrompt = editingPrompt,
            promptState = promptState,
            onPromptChange = onPromptChange
        )
        OutputDestination(
            item = item,
            editingOutputDestState = editingOutputDestState,
            outputDestState = outputDestState,
            onOutputDestChange = onOutputDestChange,
            onRunClick = onRunClick,
            onSaveClick = onSaveClick
        )
        // Use the local elapsedTime state to display the time
        Text(
            text = elapsedTime,
            modifier = Modifier
                .width(QueueUIConstants.TIME_WIDTH.dp)
                .padding(end = QueueUIConstants.INSET.dp),
            style = TextStyle(fontSize = 14.sp),
            color = textColor // Light gray text
        )
    }
}

@Preview
@Composable
fun QueueTreeCellPreview() {
    val sampleItem = QueueItem(
        file = object : IFile {
            override val name: String = "SampleFile.kt"
            override val text: String? = "Sample content"
            override val virtualFilePath: String = "/path/to/SampleFile.kt"
        },
        status = QueueItem.Status.PENDING,
        prompt = "Explain this code",
        outputDestination = "output.txt"
    )
    val sampleProject = object : IProject {
        override val name: String = "SampleProject"
        override val basePath: String? = "/path/to"
        override fun getFileEditorManager(): IFileEditorManager {
            throw NotImplementedError()
        }
    }
    QueueTreeCell(
        item = sampleItem,
        project = sampleProject,
        isSelected = false,
        onRunClick = {},
        onSaveClick = {},
        onPromptChange = {},
        onOutputDestChange = {}
    )
}