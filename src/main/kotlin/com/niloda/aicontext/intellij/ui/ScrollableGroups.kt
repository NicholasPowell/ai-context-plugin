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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

@Composable
fun BoxScope.ScrollableGroups(
    groupedItems: Map<String, List<QueueItem>>,
    headerBackground: Color,
    textColor: Color,
    project: IProject
) {
    val scrollState = rememberScrollState()
    LazyColumn(
        modifier = Modifier.Companion.fillMaxSize().padding(8.dp),
        state = rememberLazyListState()
    ) {
        LazyGroupedItems(
            scope = this,
            groupedItems = groupedItems,
            headerBackground = headerBackground,
            textColor = textColor,
            project = project
        )
    }

    VerticalScrollbar(
        modifier = Modifier.Companion.align(Alignment.Companion.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState)
    )
}