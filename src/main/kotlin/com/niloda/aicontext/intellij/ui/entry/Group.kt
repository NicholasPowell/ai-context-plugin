package com.niloda.aicontext.intellij.ui.entry

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.intellij.ui.FileRow
import com.niloda.aicontext.intellij.ui.components.Col
import com.niloda.aicontext.intellij.ui.components.R
import com.niloda.aicontext.intellij.ui.components.ToggleIcon
import com.niloda.aicontext.intellij.ui.components.withSetter
import com.niloda.aicontext.model.SendToAi
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

@Composable
fun Group(
    groupName: String,
    items: List<QueueItem>,
    project: IProject,
    sendToAi: SendToAi
) {
    val expandState = remember { mutableStateOf(true) }
    var isExpanded by expandState
    Col.Wide {
        R.Wide {
            ToggleIcon(
                state = isExpanded withSetter { isExpanded = it },
                onOff = Default.KeyboardArrowDown to Default.KeyboardArrowRight
            )
            Body1("$groupName (${items.size})")
        }
        if (isExpanded) {
            Col(Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                items.forEach { item ->
                    FileRow(
                        item = item,
                        project = project,
                        onRunClick = { sendToAi(item, project) },
                        onSaveClick = { },
                        onPromptChange = { /* Handled in QueueTreeCell */ },
                        onOutputDestChange = { /* Handled in QueueTreeCell */ }
                    )
                }
            }
        }
    }
}



