package com.niloda.aicontext.intellij.uibridge

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.niloda.aicontext.intellij.uibridge.Facade.createPanel

class AiProcessorWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val composePanel = createPanel(project)
        val content = ContentFactory.getInstance().createContent(composePanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}