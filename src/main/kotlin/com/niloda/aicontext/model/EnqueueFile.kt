package com.niloda.aicontext.model

import com.niloda.aicontext.intellij.uibridge.Facade

object EnqueueFile {
    operator fun invoke(file: IFile) {
        val existingItem = Facade.dataStore._queueFlow.value.find { it.file == file }
        if (existingItem != null) {
            Facade.dataStore._queueFlow.value -= existingItem
            if (existingItem.status == QueueItem.Status.RUNNING) {
                Facade.fileProcessor.terminate(file)
            }
        }
        val item = QueueItem(file, groupName = "Default")
        Facade.dataStore._queueFlow.value += item
        println(
            "Queued file: ${file.name} in group: Default, Queue size: ${
                Facade.dataStore._queueFlow.value
                    .size
            }"
        )
    }
}