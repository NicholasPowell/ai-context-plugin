package com.niloda.aicontext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile

class QueueFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: run {
            Messages.showErrorDialog(project, "No file selected to queue!", "AI Context")
            return
        }
        AiContextQueueManager.queueFile(psiFile)
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabled = project != null && psiFile != null
    }
}