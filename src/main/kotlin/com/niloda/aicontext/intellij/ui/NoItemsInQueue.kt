package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.niloda.aicontext.intellij.ui.entry.Body1

@Composable
fun BoxScope.NoItemsInQueue() {
    Body1(text = "No items in queue. Enqueue files to see them here.", modifier = Modifier.align(Alignment.Center))
}