package com.niloda.aicontext.model

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task

object Terminate {
    operator fun invoke(
        activeTasks: MutableMap<IFile, Pair<Task.Backgroundable, ProgressIndicator>>,
        file: IFile
    ) {
        val (task, indicator) = activeTasks[file] ?: return
        println("Terminating task for ${file.name}")
        indicator.cancel()
        activeTasks.remove(file)
        val item = IntelliJAiFileProcessor.queue.find { it.file == file }
        if (item != null && item.status == QueueItem.Status.RUNNING) {
            item.status = QueueItem.Status.CANCELLED
            item.startTime = null
        }
    }
}