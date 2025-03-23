package com.niloda.aicontext.model

import com.niloda.aicontext.intellij.uibridge.Facade

object EnqueueFile {
    operator fun invoke(file: IFile) {
        val existingItem = Facade.dataStore.find(file)
        if (existingItem != null) {
            Facade.dataStore.remove(existingItem)
            if (existingItem.status == QueueItem.Status.RUNNING) {
                Facade.fileProcessor.terminate(file)
            }
        }
        val item = QueueItem(file = file, groupName = "Default")
        Facade.dataStore.add(item)
    }
}