package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.intellij.ui.components.Col
import com.niloda.aicontext.intellij.ui.components.R
import com.niloda.aicontext.intellij.ui.entry.Group
import com.niloda.aicontext.model.AiSender
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.jewel.ui.component.HorizontalScrollbar

@Composable
fun BoxScope.ScrollableGroups(
    queueState: StateFlow<List<QueueItem>>,
    project: IProject,
    sendToAi: AiSender
) {
    val groupedItems = queueState.collectAsState().value.groupBy { it.groupName }
    val scrollState = rememberScrollState()
    val scrollStateH = rememberScrollState()

    Col.Wide(modifier = Modifier.verticalScroll(scrollState)) {
        R.Wide(Modifier.horizontalScroll(scrollStateH)) {
            Col.Wide {
                groupedItems.forEach { (groupName, items) ->
                    Group(
                        groupName = groupName,
                        items = items,
                        project = project,
                        sendToAi = sendToAi
                    )
                }
            }
        }
        HorizontalScrollbar(
            scrollState = scrollStateH,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
    VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState)
    )

}