package com.niloda.aicontext.intellij.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niloda.aicontext.QueueUIConstants
import com.niloda.aicontext.intellij.ui.entry.OutputDestination
import com.niloda.aicontext.intellij.ui.entry.Prompt
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IFileEditorManager
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.delay

val jetbrainsMono = FontFamily(
    Font(
        resource = "fonts/variable/JetBrainsMono[wght].ttf",
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400) // Default weight value
        )
    )
)

@Composable
fun QueueItemRow(
    item: QueueItem,
    project: IProject,
    isSelected: Boolean,
    onRunClick: () -> Unit,
    onSaveClick: () -> Unit,
    onPromptChange: (String) -> Unit,
    onOutputDestChange: (String) -> Unit
) {
    var promptState: MutableState<String> = remember { mutableStateOf(item.prompt) }
    var outputDestState: MutableState<String> = remember { mutableStateOf(item.outputDestination) }
    var editingPrompt: MutableState<Boolean> = remember { mutableStateOf(false) }
    var editingOutputDestState: MutableState<Boolean> = remember { mutableStateOf(false) }

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
            .background(MaterialTheme.colors.surface)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(QueueUIConstants.INSET.dp))
        Column(
            modifier = Modifier
                .width(QueueUIConstants.FILE_PATH_WIDTH.dp)
                .padding(end = QueueUIConstants.INSET.dp)
        ) {
            Row {
                Text(
                    text = item.getDisplayPath(project),
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    fontFamily = jetbrainsMono
                )
                Spacer(modifier = Modifier.width(QueueUIConstants.INSET.dp))
                Text(
                    fontFamily = jetbrainsMono,
                    text = item.status.toString(),
                    modifier = Modifier
                        .width(QueueUIConstants.STATUS_WIDTH.dp)
                        .padding(end = QueueUIConstants.INSET.dp),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    fontFamily = jetbrainsMono,
                    text = elapsedTime,
                    modifier = Modifier
                        .width(QueueUIConstants.TIME_WIDTH.dp)
                        .padding(end = QueueUIConstants.INSET.dp),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground
                )
            }
            Row {
                Prompt(
                    item = item,
                    editingPrompt = editingPrompt,
                    promptState = promptState,
                    onPromptChange = onPromptChange,
                    modifier = Modifier.width((QueueUIConstants.PROMPT_WIDTH / 2).dp)
                )
                OutputDestination(
                    item = item,
                    editingOutputDestState = editingOutputDestState,
                    outputDestState = outputDestState,
                    onOutputDestChange = onOutputDestChange,
                    onRunClick = onRunClick,
                    onSaveClick = onSaveClick,
                    modifier = Modifier.width((QueueUIConstants.OUTPUT_DEST_WIDTH / 2).dp)
                )
            }

        }
    }
}

@Preview
@Composable
fun QueueTreeCellPreview() {
    DarculaTheme {
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
        QueueItemRow(
            item = sampleItem,
            project = sampleProject,
            isSelected = false,
            onRunClick = {},
            onSaveClick = {},
            onPromptChange = {},
            onOutputDestChange = {}
        )
    }
}