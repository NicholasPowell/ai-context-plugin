// File: src/main/kotlin/com/niloda/aicontext/intellij/ui/ScrollableGroups.kt
package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

@Composable
fun BoxScope.ScrollableGroups(
    groupedItems: Map<String, List<QueueItem>>,
    project: IProject
) {
    val scrollState = rememberScrollState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        state = rememberLazyListState()
    ) {
        LazyGroupedItems(
            scope = this,
            groupedItems = groupedItems,
            project = project
        )
    }

    VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState)
    )
}