package com.niloda.aicontext.intellij.uibridge

import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DataStore(
    val _queueFlow: MutableStateFlow<List<QueueItem>> = MutableStateFlow<List<QueueItem>>(listOf()),
    val queueFlow: StateFlow<List<QueueItem>> = _queueFlow
)