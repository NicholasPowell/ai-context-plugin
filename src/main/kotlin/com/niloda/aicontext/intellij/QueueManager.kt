package com.niloda.aicontext.intellij

import com.intellij.psi.PsiFile
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem

object QueueManager {
    val aiService: IntelliJAiFileProcessor = IntelliJAiFileProcessor

    fun queueFile(file: PsiFile) {
        aiService.enqueueFile(file.adapt())
        val queuedItem = aiService.queue.last()
        AiProcessorToolWindow.addToQueue(queuedItem, file.project.adapt())
        AiProcessorToolWindow.updateQueue(file.project.adapt())
    }

    fun processFile(item: QueueItem, project: IProject) {
        aiService.processFile(item, project)
        AiProcessorToolWindow.updateQueue(project) // Update immediately to reflect running status
    }

    fun terminate(file: IFile) {
        aiService.terminate(file)
    }
}