package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BoxScope.ScrollableGroups(
    queueState: StateFlow<List<QueueItem>>,
    project: IProject
) {
    val groupedItems = queueState.collectAsState().value.groupBy { it.groupName }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = groupedItems.toString(),
            color = MaterialTheme.colors.onBackground // Use theme text color
        )
        GroupedItems(
            groupedItems = groupedItems,
            project = project
        )
    }

    VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState)
    )
}