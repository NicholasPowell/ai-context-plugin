package com.niloda.aicontext

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.uibridge.Facade

class QueueFileAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT // Run update on background thread
    }

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

        Facade.enqueueFile(psiFile.adapt())
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = project != null && (psiFile != null || editor?.virtualFile != null)
        e.presentation.text = "Enqueue for AI Processing"
    }
}