package com.niloda.aicontext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.WindowManager
import com.niloda.aicontext.impl.AiContextServiceImpl
import com.niloda.aicontext.impl.AiContextServiceImpl.IntelliJEditorAdapter
import com.niloda.aicontext.impl.AiContextServiceImpl.IntelliJFileAdapter
import com.niloda.aicontext.impl.AiContextServiceImpl.IntelliJProjectAdapter
import com.niloda.aicontext.impl.adapt
import com.niloda.aicontext.model.AiContextService

class GetContextAction : AnAction() {
    private val aiService: AiContextService = AiContextServiceImpl

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)?.let { IntelliJEditorAdapter(it) }
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)?.let { IntelliJFileAdapter(it) }
        val context = aiService.getContext(project.adapt(), editor?.selectedText, psiFile)

        if (context.isBlank()) {
            Messages.showMessageDialog(project, "No context available to send to Ollama!", "AI Context", Messages.getErrorIcon())
            return
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Fetching AI Context") {
            override fun run(indicator: com.intellij.openapi.progress.ProgressIndicator) {
                val statusBar = WindowManager.getInstance().getStatusBar(project)
                statusBar?.setInfo("Processing AI request...")

                val prompt = "Explain this code:\n" + context
                val response = aiService.sendToAi(prompt, project.adapt())
                println("GetContextAction response: ${response?.take(50) ?: "null"}")

                com.intellij.util.ui.UIUtil.invokeLaterIfNeeded {
                    if (response != null && psiFile != null) {
                        val item = AiContextService.QueueItem(psiFile, prompt = prompt, status = AiContextService.QueueItem.Status.DONE)
                        aiService.queueFile(psiFile) // Ensure it’s in the queue
                        AiContextToolWindow.setResult(item, project.adapt(), response)
                    } else if (response == null) {
                        Messages.showErrorDialog(project, "Failed to get response from Ollama", "AI Context")
                    }
                    statusBar?.setInfo("AI request completed")
                }
            }
        })
    }

    override fun update(e: AnActionEvent) {
        val project = e.project?.let { IntelliJProjectAdapter(it) }
        val editor = e.getData(CommonDataKeys.EDITOR)?.let { IntelliJEditorAdapter(it) }
        e.presentation.isEnabled = project != null && (editor != null || FileEditorManager.getInstance((project as IntelliJProjectAdapter).project).openFiles.isNotEmpty())
    }
}