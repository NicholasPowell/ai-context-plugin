package com.niloda.aicontext.intellij.uibridge

import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QueueDataStore {
    private val _queueFlow: MutableStateFlow<List<QueueItem>> =
        MutableStateFlow<List<QueueItem>>(listOf())
    val queueFlow: StateFlow<List<QueueItem>> = _queueFlow
    fun find(file: IFile): QueueItem? = _queueFlow.value.find { it.file == file }
    fun remove(existingItem: QueueItem) {
        _queueFlow.value -= existingItem
    }

    fun add(item: QueueItem) {
        _queueFlow.value =  _queueFlow.value
                .firstOrNull { it.file.virtualFilePath == item.file.virtualFilePath }
                ?.let { _queueFlow.value - it + item }
                ?:( _queueFlow.value + item)
    }

    val size get() = _queueFlow.value.size
}