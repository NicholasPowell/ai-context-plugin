package com.niloda.aicontext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.niloda.aicontext.intellij.QueueManager

class QueueFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: e.getData(CommonDataKeys.EDITOR)?.let { editor ->
            val virtualFile = editor.virtualFile ?: return@let null
            com.intellij.psi.PsiManager.getInstance(project).findFile(virtualFile)
        }

        if (psiFile == null) {
            Messages.showErrorDialog(project, "No file selected to queue!", "AI Context")
            return
        }
        QueueManager.queueFile(psiFile)
        Messages.showInfoMessage(project, "Enqueued file: ${psiFile.name}", "AI Context")
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val editor = e.getData(CommonDataKeys.EDITOR)
        // Enable if there's a project and either a PSI file (project view) or an editor with a file
        e.presentation.isEnabled = project != null && (psiFile != null || editor?.virtualFile != null)
        e.presentation.text = "Enqueue for AI Processing"
    }
}