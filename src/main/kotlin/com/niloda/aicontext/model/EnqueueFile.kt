package com.niloda.aicontext.model

import com.niloda.aicontext.intellij.uibridge.QueueDataStore

class EnqueueFile(val queueDataStore: QueueDataStore) {
    operator fun invoke(file: IFile) {
        val existingItem = queueDataStore.find(file)
        if (existingItem != null) {
            queueDataStore.remove(existingItem)
            // TODO, keep reference to running threads/coroutines to be able to terminate
//            if (existingItem.status == QueueItem.Status.RUNNING) {
//                Facade.fileProcessor.terminate(file)
//            }
        }
        val item = QueueItem(file = file, groupName = "Default")
        queueDataStore.add(item)
    }
}