package com.niloda.aicontext.model

import com.niloda.aicontext.intellij.uibridge.DataStore

class EnqueueFile(val dataStore: DataStore) {
    operator fun invoke(file: IFile) {
        val existingItem = dataStore.find(file)
        if (existingItem != null) {
            dataStore.remove(existingItem)
            // TODO, keep reference to running threads/coroutines to be able to terminate
//            if (existingItem.status == QueueItem.Status.RUNNING) {
//                Facade.fileProcessor.terminate(file)
//            }
        }
        val item = QueueItem(file = file, groupName = "Default")
        dataStore.add(item)
    }
}