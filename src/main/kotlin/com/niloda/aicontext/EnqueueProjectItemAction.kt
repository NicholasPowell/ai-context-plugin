package com.niloda.aicontext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.niloda.aicontext.intellij.IntelliJAiFileProcessor
import com.niloda.aicontext.intellij.QueueManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.niloda.aicontext.intellij.AiProcessorToolWindow
import com.niloda.aicontext.intellij.adapt

class EnqueueProjectItemAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT) ?: run {
            Messages.showErrorDialog(project, "No item selected to enqueue!", "AI Context")
            return
        }

        val groupName = Messages.showInputDialog(
            project,
            "Enter group name for this item(s):",
            "Group Name",
            Messages.getQuestionIcon(),
            "Default",
            null
        ) ?: return // Cancelled dialog returns null

        when (psiElement) {
            is PsiFile -> {
                IntelliJAiFileProcessor.enqueueFileWithGroup(psiElement.adapt(), groupName)
                Messages.showInfoMessage(project, "Enqueued file: ${psiElement.name} in group: $groupName", "AI Context")
            }
            is PsiDirectory -> {
                val files = psiElement.files.filter { it.isPhysical && !it.isDirectory }
                if (files.isEmpty()) {
                    Messages.showWarningDialog(project, "No files found in directory: ${psiElement.name}", "AI Context")
                    return
                }
                files.forEach { file ->
                    IntelliJAiFileProcessor.enqueueFileWithGroup(file.adapt(), groupName)
                }
                Messages.showInfoMessage(project, "Enqueued ${files.size} file(s) from directory: ${psiElement.name} in group: $groupName", "AI Context")
            }
            else -> {
                Messages.showErrorDialog(project, "Selected item is not a file or directory!", "AI Context")
            }
        }
        AiProcessorToolWindow.updateQueue(project.adapt()) // Update UI after enqueuing
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isEnabled = project != null && (psiElement is PsiFile || psiElement is PsiDirectory)
        e.presentation.text = "Enqueue for AI Processing"
    }
}