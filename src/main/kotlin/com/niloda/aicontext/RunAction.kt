package com.niloda.aicontext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

class RunAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText

        if (selectedText.isNullOrBlank()) {
            Messages.showMessageDialog(project, "Please select some code to run!", "Run This", Messages.getErrorIcon())
        } else {
            Messages.showMessageDialog(project, "Running:\n$selectedText", "Run This", Messages.getInformationIcon())
            // TODO: Add logic to "run" the selected code
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null && editor.selectionModel.hasSelection()
    }
}