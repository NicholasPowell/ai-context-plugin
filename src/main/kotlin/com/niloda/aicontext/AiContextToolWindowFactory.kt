package com.niloda.aicontext

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.Component
import java.util.Timer
import java.util.TimerTask
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class AiContextToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = JPanel(BorderLayout())

        val queueModel = object : DefaultTableModel(arrayOf("File", "Prompt", "Action", "Status", "Time"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = column == 1 // Only Prompt editable
        }
        val queueTable = JBTable(queueModel)
        queueTable.apply {
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            columnModel.getColumn(0).preferredWidth = 300
            columnModel.getColumn(1).preferredWidth = 200
            columnModel.getColumn(2).maxWidth = 80
            columnModel.getColumn(3).maxWidth = 100
            columnModel.getColumn(4).maxWidth = 80

            setDefaultRenderer(Any::class.java, object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
                ): Component {
                    val item = AiContextQueueManager.queue.elementAtOrNull(row) ?: return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    return when (column) {
                        2 -> {
                            val button = if (item.status == AiContextQueueManager.QueueItem.Status.PENDING) JButton("Run") else JButton("Cancel")
                            button.isEnabled = true
                            button.addActionListener {
                                println("Button clicked for ${item.file?.name ?: "unknown"} - Status: ${item.status}")
                                if (item.status == AiContextQueueManager.QueueItem.Status.PENDING) {
                                    AiContextQueueManager.processFile(item, project)
                                } else if (item.status == AiContextQueueManager.QueueItem.Status.RUNNING) {
                                    AiContextQueueManager.terminate(item.file!!)
                                }
                            }
                            button
                        }
                        else -> super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    }
                }
            })

            setDefaultEditor(Any::class.java, object : DefaultCellEditor(JTextField()) {
                override fun getTableCellEditorComponent(table: JTable, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
                    val item = AiContextQueueManager.queue.elementAtOrNull(row) ?: return super.getTableCellEditorComponent(table, value, isSelected, row, column)
                    return when (column) {
                        1 -> {
                            val textField = JTextField(item.prompt)
                            textField.addActionListener {
                                item.prompt = textField.text
                                println("Prompt updated for ${item.file?.name ?: "unknown"}: ${item.prompt}")
                            }
                            textField
                        }
                        else -> super.getTableCellEditorComponent(table, value, isSelected, row, column)
                    }
                }

                override fun stopCellEditing(): Boolean {
                    val success = super.stopCellEditing()
                    if (success) {
                        val row = queueTable.editingRow
                        if (row >= 0 && queueTable.editingColumn == 1) {
                            val item = AiContextQueueManager.queue.elementAtOrNull(row)
                            item?.prompt = (getCellEditorValue() as String)
                            queueModel.setValueAt(item?.prompt, row, 1)
                            queueTable.repaint()
                            println("Editing stopped - Prompt set to: ${item?.prompt}")
                        }
                    }
                    return success
                }
            })
        }
        val queueScrollPane = JBScrollPane(queueTable)
        panel.add(queueScrollPane, BorderLayout.CENTER)

        val outputArea = JTextArea("AI output will appear here...\n").apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
        }
        val outputScrollPane = JBScrollPane(outputArea)
        panel.add(outputScrollPane, BorderLayout.SOUTH)

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

    fun init(model: DefaultTableModel, table: JBTable, output: JTextArea, proj: Project) {
        queueModel = model
        queueTable = table
        outputArea = output
        project = proj
    }

    fun addToQueue(item: AiContextQueueManager.QueueItem, project: Project) {
        queueModel.addRow(arrayOf(item.getDisplayPath(project), item.prompt, "Run", item.status.toString(), item.getElapsedTime()))
        println("Added to queue: ${item.file?.name ?: "unknown"} with prompt: ${item.prompt}")
    }

    fun updateQueue(project: Project) {
        queueModel.rowCount = 0
        AiContextQueueManager.queue.forEach { item ->
            queueModel.addRow(arrayOf(item.getDisplayPath(project), item.prompt, if (item.status == AiContextQueueManager.QueueItem.Status.PENDING) "Run" else "Cancel", item.status.toString(), item.getElapsedTime()))
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