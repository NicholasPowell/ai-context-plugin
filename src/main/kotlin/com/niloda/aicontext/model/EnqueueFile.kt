package com.niloda.aicontext.model

object EnqueueFile {
    operator fun invoke(file: IFile) {
        val existingItem = IntelliJAiFileProcessor.queue.find { it.file == file }
        if (existingItem != null) {
            IntelliJAiFileProcessor.queue.remove(existingItem)
            if (existingItem.status == QueueItem.Status.RUNNING) {
                IntelliJAiFileProcessor.terminate(file)
            }
        }
        val item = QueueItem(file)
        IntelliJAiFileProcessor.queue.add(item)
        println("Queued file: ${file.name}, Queue size: ${IntelliJAiFileProcessor.queue.size}")
    }
}