package com.niloda.aicontext.intellij

import com.niloda.aicontext.intellij.adapt.IntelliJFileAdapter
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.QueueItem

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
        AiProcessorToolWindow.addToQueue(item, (file as? IntelliJFileAdapter)?.psiFile?.project?.adapt() ?: return)
    }
}