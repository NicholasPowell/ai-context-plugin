package com.niloda.aicontext.model

import com.niloda.aicontext.intellij.uibridge.AiProcessorToolWindow

object EnqueueFile {
    operator fun invoke(file: IFile) {
//        val existingItem = IntelliJAiFileProcessor.queue.find { it.file == file }
//        if (existingItem != null) {
//            IntelliJAiFileProcessor.queue.remove(existingItem)
//            if (existingItem.status == QueueItem.Status.RUNNING) {
//                IntelliJAiFileProcessor.terminate(file)
//            }
//        }
        val item = QueueItem(file)
        AiProcessorToolWindow._queueFlow.value += item
        println("Queued file: ${file.name}, Queue size: ${AiProcessorToolWindow._queueFlow.value.size}")
    }
}