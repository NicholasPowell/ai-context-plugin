package com.niloda.aicontext

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.util.Timer
import java.util.TimerTask
import javax.swing.*
import javax.swing.table.DefaultTableModel

class AiContextToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = JPanel(BorderLayout())

        val queueModel = object : DefaultTableModel(arrayOf("File", "Status", "Time"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = false
        }
        val queueTable = JBTable(queueModel).apply {
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            columnModel.getColumn(0).preferredWidth = 300 // Wider for file path
            columnModel.getColumn(1).maxWidth = 100
            columnModel.getColumn(2).maxWidth = 80
        }
        val queueScrollPane = JBScrollPane(queueTable)
        panel.add(queueScrollPane, BorderLayout.NORTH)

        val outputArea = JTextArea("AI output will appear here...\n").apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
        }
        val outputScrollPane = JBScrollPane(outputArea)
        panel.add(outputScrollPane, BorderLayout.CENTER)

        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)

        AiContextToolWindow.init(queueModel, queueTable, outputArea, project)

        val timer = Timer(true)
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                SwingUtilities.invokeLater {
                    AiContextToolWindow.updateQueue(project)
                }
            }
        }, 0, 1000)
    }
}

object AiContextToolWindow {
    private lateinit var queueModel: DefaultTableModel
    private lateinit var queueTable: JBTable
    private lateinit var outputArea: JTextArea
    private lateinit var project: Project

    fun init(model: DefaultTableModel, table: JBTable, area: JTextArea, proj: Project) {
        queueModel = model
        queueTable = table
        outputArea = area
        project = proj

        queueTable.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 1) {
                    val row = queueTable.rowAtPoint(e.point)
                    if (row >= 0) {
                        val filePath = queueModel.getValueAt(row, 0) as String
                        val status = queueModel.getValueAt(row, 1) as String
                        if (status == "Running") {
                            val file = AiContextQueueManager.queue.find { it.getDisplayPath(project) == filePath }?.file
                            file?.let { AiContextQueueManager.terminate(it) }
                        }
                    }
                }
            }
        })
    }

    fun addToQueue(item: AiContextQueueManager.QueueItem, project: Project) {
        queueModel.addRow(arrayOf(item.getDisplayPath(project), item.status.toString(), item.getElapsedTime()))
    }

    fun updateQueue(project: Project) {
        queueModel.rowCount = 0
        AiContextQueueManager.queue.forEach { item ->
            queueModel.addRow(arrayOf(item.getDisplayPath(project), item.status.toString(), item.getElapsedTime()))
        }
    }

    fun appendOutput(text: String) {
        outputArea.append("$text\n")
        outputArea.caretPosition = outputArea.document.length
    }

    fun clearOutput() {
        outputArea.text = ""
    }
}