package com.niloda.aicontext.intellij

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.util.ui.UIUtil
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.QueueItem

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