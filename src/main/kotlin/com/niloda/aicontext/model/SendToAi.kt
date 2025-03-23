package com.niloda.aicontext.model

class SendToAi(
    private val aiService: IntelliJAiFileProcessor
) {

    fun processFile(item: QueueItem, project: IProject) {
        aiService.processFile(item, project)
    }

    fun terminate(file: IFile) {
        aiService.terminate(file)
    }
}