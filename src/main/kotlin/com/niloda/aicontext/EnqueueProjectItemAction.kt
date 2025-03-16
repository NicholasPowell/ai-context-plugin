package com.niloda.aicontext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.niloda.aicontext.impl.adapt

class EnqueueProjectItemAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT) ?: run {
            Messages.showErrorDialog(project, "No item selected to enqueue!", "AI Context")
            return
        }

        when (psiElement) {
            is PsiFile -> {
                AiContextQueueManager.queueFile(psiElement)
                Messages.showInfoMessage(project, "Enqueued file: ${psiElement.name}", "AI Context")
            }
            is PsiDirectory -> {
                val files = psiElement.files.filter { it.isPhysical && !it.isDirectory }
                if (files.isEmpty()) {
                    Messages.showWarningDialog(project, "No files found in directory: ${psiElement.name}", "AI Context")
                    return
                }
                files.forEach { file ->
                    AiContextQueueManager.queueFile(file)
                }
                Messages.showInfoMessage(project, "Enqueued ${files.size} file(s) from directory: ${psiElement.name}", "AI Context")
            }
            else -> {
                Messages.showErrorDialog(project, "Selected item is not a file or directory!", "AI Context")
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isEnabled = project != null && (psiElement is PsiFile || psiElement is PsiDirectory)
        e.presentation.text = "Enqueue for AI Processing"
    }
}