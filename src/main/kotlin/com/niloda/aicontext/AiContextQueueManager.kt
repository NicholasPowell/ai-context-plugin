package com.niloda.aicontext

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.niloda.aicontext.impl.AiContextServiceImpl
import com.niloda.aicontext.impl.adapt
import com.niloda.aicontext.model.AiContextService
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IProject

object AiContextQueueManager {
    val aiService: AiContextService = AiContextServiceImpl

    fun queueFile(file: PsiFile) {
        aiService.queueFile(file.adapt())
        val queuedItem = aiService.queue.last()
        AiContextToolWindow.addToQueue(queuedItem, file.project.adapt())
        AiContextToolWindow.updateQueue(file.project.adapt())
    }

    fun processFile(item: AiContextService.QueueItem, project: IProject) {
        aiService.processFile(item, project)
        AiContextToolWindow.appendOutput("File: ${item.getDisplayPath(project)}\nPrompt:\n${item.prompt}\n\nResponse:\nProcessing...\n\n")
        AiContextToolWindow.updateQueue(project)
    }

    fun terminate(file: IFile) {
        aiService.terminate(file)
    }
}