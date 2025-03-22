package com.niloda.aicontext.model

object QueueManager {
    val aiService: IntelliJAiFileProcessor = IntelliJAiFileProcessor

    fun processFile(item: QueueItem, project: IProject) {
        aiService.processFile(item, project)
    }

    fun terminate(file: IFile) {
        aiService.terminate(file)
    }
}