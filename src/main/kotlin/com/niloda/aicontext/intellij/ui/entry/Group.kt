package com.niloda.aicontext.intellij.ui.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.intellij.ui.QueueRows
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.SendToAi

@Composable
fun Group(
    groupName: String,
    items: List<QueueItem>,
    project: IProject,
    sendToAi: SendToAi
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface) // Use theme surface for header
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$groupName (${items.size})",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colors.onSurface // Use theme text color
            )
        }
        QueueRows(queueItems = items, project = project, sendToAi = sendToAi)
    }
}