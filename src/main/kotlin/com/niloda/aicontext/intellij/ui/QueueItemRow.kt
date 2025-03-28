package com.niloda.aicontext.intellij.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder
import com.niloda.aicontext.intellij.ui.theme.DarculaTheme
import com.niloda.aicontext.intellij.ui.theme.jetbrainsMono
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IFileEditorManager
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileRow(
    item: QueueItem,
    project: IProject,
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
            .debugBorder()
            .background(MaterialTheme.colors.surface)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(end = 8.dp)
                .debugBorder()
        ) {
            Row(modifier = Modifier.fillMaxWidth().debugBorder().testTag("cool")) {
                @Composable {
                    Surface(
                        modifier = Modifier.debugBorder(),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colors.surface,
                        elevation = 4.dp
                    ) {
                        Text(
                            text = item.getDisplayPath(project),
                            modifier = debugBorder()
                        )
                    }
                } tooltipFor {
                    Row(Modifier.debugBorder()) {
                        Text(
                            text = item.file.name,
                            color = MaterialTheme.colors.onBackground,
                            style = MaterialTheme.typography.body2,
                            maxLines = 1,
                            fontFamily = jetbrainsMono
                        )
                        Text(
                            text = item.status.toString(),
                            fontFamily = jetbrainsMono,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface
                        )
                        Text(
                            fontFamily = jetbrainsMono,
                            text = elapsedTime,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onBackground
                        )
                    }
                }
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
        FileRow(
            item = sampleItem,
            project = sampleProject,
            onRunClick = {},
            onSaveClick = {},
            onPromptChange = {},
            onOutputDestChange = {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
infix fun @Composable ()->Unit.tooltipFor(content: @Composable ()->Unit) {
    TooltipArea(
        tooltip = this,
        modifier = debugBorder(),
        delayMillis = 500,
        content = content
    )
}
