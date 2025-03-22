package com.niloda.aicontext.intellij.uibridge

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class AiProcessorWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Initializing tool window for project: ${project.name}")
        val composePanel = AiProcessorToolWindow.createComposePanel(project)
        val content = ContentFactory.getInstance().createContent(composePanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}