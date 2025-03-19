package com.niloda.aicontext.intellij

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import javax.swing.JPanel

class AiProcessorWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Initializing tool window for project: ${project.name}")
        val panel = JPanel(BorderLayout())
        val queueTree = Tree().apply {
            putClientProperty("project", project.adapt()) // Store IProject for renderer
        }
        queueTree.applyDragAndDrop(project)
        val scrollPane = JBScrollPane(queueTree)
        panel.add(scrollPane, BorderLayout.CENTER)

        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)

        AiProcessorToolWindow.init(queueTree, project)
        AiProcessorToolWindow.updateQueue(project.adapt())
    }
}