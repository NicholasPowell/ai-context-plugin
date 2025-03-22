package com.niloda.aicontext.intellij

import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

object QueueManager {
    val aiService: IntelliJAiFileProcessor = IntelliJAiFileProcessor

    fun processFile(item: QueueItem, project: IProject) {
        aiService.processFile(item, project)
    }

    fun terminate(file: IFile) {
        aiService.terminate(file)
    }
}