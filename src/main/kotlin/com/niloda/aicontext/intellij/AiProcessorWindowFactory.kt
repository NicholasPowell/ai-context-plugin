package com.niloda.aicontext.intellij

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class AiProcessorWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Initializing tool window for project: ${project.name}")
        val panel = JPanel(BorderLayout())

        val queueModel = object : DefaultTableModel(arrayOf(
            "File",
            "Prompt",
            "Output Destination",
            "Action",
            "Status",
            "Time"
        ), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return !rowIsGroupHeader(row) && (column == 1 || column == 2)
            }

            private fun rowIsGroupHeader(row: Int): Boolean {
                val value = getValueAt(row, 0)?.toString() ?: return false
                return value.startsWith("Group: ")
            }
        }
        val queueTable = JBTable(queueModel)
        queueTable.applyTableDimensions()
        queueTable.applyButtonClickListener(project, queueModel)
        queueTable.applyRenderer()
        queueTable.applyEditor(queueModel)
        queueTable.applyDragAndDrop(project)

        val queueScrollPane = JBScrollPane(queueTable)
        panel.add(queueScrollPane, BorderLayout.CENTER)

        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)

        AiProcessorToolWindow.init(queueModel, queueTable, project)
        AiProcessorToolWindow.updateQueue(project.adapt())
    }
}