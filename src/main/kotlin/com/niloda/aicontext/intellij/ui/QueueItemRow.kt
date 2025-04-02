package com.niloda.aicontext.intellij.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder
import com.niloda.aicontext.intellij.ui.components.Col
import com.niloda.aicontext.intellij.ui.components.R
import com.niloda.aicontext.intellij.ui.entry.Body2
import com.niloda.aicontext.intellij.ui.theme.DarculaTheme
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IFileEditorManager
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.delay
import kotlin.toString

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

    // TODO, offload this
    LaunchedEffect(item.status) {
        if (item.status == QueueItem.Status.RUNNING) {
            while (true) {
                elapsedTime = item.getElapsedTime()
                delay(1000) // Update every second
                if (item.status != QueueItem.Status.RUNNING) break
            }
        } else {
            elapsedTime = item.getElapsedTime()
        }
    }

    R(valign = Alignment.CenterVertically) {
        Col.Wide {
            R.Wide {
                TT({
                    TooltipSurface {
                        Body2(item.getDisplayPath(project))
                    }
                }) {
                    R {
                        Body2(item.file.name)
                        Body2(item.status.toString())
                        Body2(elapsedTime)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TT(tooltip: @Composable () -> Unit, content: @Composable () -> Unit) {
    TooltipArea(
        tooltip = tooltip,
        modifier = debugBorder(),
        delayMillis = 500,
        content = content
    )
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

