package com.niloda.aicontext.intellij.ui.entry

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder
import com.niloda.aicontext.intellij.ui.FileRow
import com.niloda.aicontext.intellij.ui.components.R
import com.niloda.aicontext.intellij.ui.components.Col
import com.niloda.aicontext.intellij.ui.components.ToggleIcon
import com.niloda.aicontext.intellij.ui.components.withSetter
import com.niloda.aicontext.intellij.uibridge.Facade
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.QueueItem.Status
import com.niloda.aicontext.model.SendToAi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                onOff = Default.KeyboardArrowDown to Default.KeyboardArrowRight,
                modifier = Modifier.size(20.dp),
                toggleState = isExpanded withSetter { isExpanded = it }
            )
            Text(
                text = "$groupName (${items.size})",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth().debugBorder(),
                color = MaterialTheme.colors.onSurface
            )
        }
        if (isExpanded) {
            Col(Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)) {
                items.forEach { item ->
                    FileRow(
                        item = item,
                        project = project,
                        onRunClick = { sendToAiInBackground(item, sendToAi, project) },
                        onSaveClick = { },
                        onPromptChange = { /* Handled in QueueTreeCell */ },
                        onOutputDestChange = { /* Handled in QueueTreeCell */ }
                    )
                }
            }
        }
    }
}

fun sendToAiInBackground(
    item: QueueItem,
    sendToAi: SendToAi,
    project: IProject
) {
    CoroutineScope(Dispatchers.IO).launch {
        Facade.dataStore.add(item.copy(status = Status.RUNNING))
        sendToAi(item, project = project)
        Facade.dataStore.add(item.copy(status = Status.DONE))
    }
}

