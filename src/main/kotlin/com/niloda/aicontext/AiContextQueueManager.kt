package com.niloda.aicontext

import com.intellij.psi.PsiFile
import com.niloda.aicontext.impl.AiContextServiceImpl
import com.niloda.aicontext.impl.adapt
import com.niloda.aicontext.model.AiContextService
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IProject

object AiContextQueueManager {
    val aiService: AiContextServiceImpl = AiContextServiceImpl

    fun queueFile(file: PsiFile) {
        aiService.queueFile(file.adapt())
        val queuedItem = aiService.queue.last()
        AiContextToolWindow.addToQueue(queuedItem, file.project.adapt())
        AiContextToolWindow.updateQueue(file.project.adapt())
    }

    fun processFile(item: AiContextService.QueueItem, project: IProject) {
        aiService.processFile(item, project)
        AiContextToolWindow.updateQueue(project) // Update immediately to reflect running status
    }

    fun terminate(file: IFile) {
        aiService.terminate(file)
    }
}