package com.niloda.aicontext

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import com.niloda.aicontext.AiContextQueueManager.aiService
import com.niloda.aicontext.impl.adapt
import com.niloda.aicontext.model.AiContextService
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class AiContextToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Initializing tool window for project: ${project.name}")
        val panel = JPanel(BorderLayout())

        val queueModel = object : DefaultTableModel(arrayOf("File", "Prompt", "Output Destination", "Action", "Status", "Time"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                val isFileRow = row % 2 == 0
                return isFileRow && (column == 1 || column == 2) // "Prompt" and "Output Destination" in file rows are editable
            }
        }
        val queueTable = JBTable(queueModel)
        queueTable.apply {
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            columnModel.getColumn(0).preferredWidth = 300
            columnModel.getColumn(1).preferredWidth = 400
            columnModel.getColumn(2).preferredWidth = 200 // New column width
            columnModel.getColumn(3).maxWidth = 80
            columnModel.getColumn(4).maxWidth = 100
            columnModel.getColumn(5).maxWidth = 80
            rowHeight = 25 // Default height for file rows

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    val row = queueTable.rowAtPoint(e.point)
                    val col = queueTable.columnAtPoint(e.point)
                    val isFileRow = row % 2 == 0
                    val item = aiService.queue.elementAtOrNull(row / 2) ?: return

                    if (isFileRow && col == 3) { // Action column in file row
                        when (item.status) {
                            QueueItem.Status.PENDING -> {
                                println("Run clicked for ${item.file.name}")
                                AiContextQueueManager.processFile(item, project.adapt())
                            }
                            QueueItem.Status.RUNNING -> {
                                println("Cancel clicked for ${item.file.name}")
                                AiContextQueueManager.terminate(item.file)
                                queueModel.setValueAt("Run", row, 3)
                                queueModel.setValueAt("CANCELLED", row, 4)
                                queueTable.repaint()
                            }
                            else -> {
                                println("No action for status ${item.status} on ${item.file.name}")
                            }
                        }
                    } else if (!isFileRow && col == 3) { // Save button in results row
                        println("Save clicked for ${item.file.name}")
                        AiContextToolWindow.saveResult(item, project.adapt())
                    }
                }
            })

            setDefaultRenderer(Any::class.java, object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
                ): Component {
                    val isFileRow = row % 2 == 0
                    val item = aiService.queue.elementAtOrNull(row / 2) ?: return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)

                    return when {
                        !isFileRow && column == 0 -> { // Results row
                            val textArea = JTextArea(value?.toString() ?: "").apply {
                                isEditable = false
                                lineWrap = true
                                wrapStyleWord = true
                            }
                            val scrollPane = JBScrollPane(textArea).apply {
                                preferredSize = java.awt.Dimension(table.width, 100)
                                minimumSize = java.awt.Dimension(table.width, 50)
                            }
                            table.setRowHeight(row, 100)
                            scrollPane
                        }
                        isFileRow && column == 3 -> { // Action column in file row
                            val button = when (item.status) {
                                QueueItem.Status.PENDING -> JButton("Run")
                                QueueItem.Status.RUNNING -> JButton("Cancel")
                                else -> JButton("Run").apply { isEnabled = false }
                            }
                            button.isEnabled = item.status == QueueItem.Status.PENDING || item.status == QueueItem.Status.RUNNING
                            button
                        }
                        !isFileRow && column == 3 -> { // Save button in results row
                            JButton("Save").apply {
                                isEnabled = item.result != null && item.status == QueueItem.Status.DONE && item.outputDestination.isNotBlank()
                            }
                        }
                        else -> super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    }
                }
            })

            setDefaultEditor(Any::class.java, object : DefaultCellEditor(JTextField()) {
                override fun getTableCellEditorComponent(table: JTable, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
                    val item = aiService.queue.elementAtOrNull(row / 2) ?: return super.getTableCellEditorComponent(table, value, isSelected, row, column)
                    return when {
                        column == 1 && row % 2 == 0 -> { // Prompt in file row
                            val textField = JTextField(item.prompt)
                            textField.addActionListener {
                                item.prompt = textField.text
                            }
                            textField
                        }
                        column == 2 && row % 2 == 0 -> { // Output Destination in file row
                            val textField = JTextField(item.outputDestination)
                            textField.addActionListener {
                                item.outputDestination = textField.text
                            }
                            textField
                        }
                        else -> super.getTableCellEditorComponent(table, value, isSelected, row, column)
                    }
                }

                override fun stopCellEditing(): Boolean {
                    val success = super.stopCellEditing()
                    if (success) {
                        val row = editingRow
                        if (row >= 0 && row % 2 == 0) {
                            val item = aiService.queue.elementAtOrNull(row / 2)
                            if (item != null) {
                                when (editingColumn) {
                                    1 -> item.prompt = (getCellEditorValue() as String)
                                    2 -> item.outputDestination = (getCellEditorValue() as String)
                                }
                                queueModel.setValueAt(if (editingColumn == 1) item.prompt else item.outputDestination, row, editingColumn)
                                queueTable.repaint()
                            }
                        }
                    }
                    return success
                }
            })
        }
        val queueScrollPane = JBScrollPane(queueTable)
        panel.add(queueScrollPane, BorderLayout.CENTER)

        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)

        AiContextToolWindow.init(queueModel, queueTable, project)
        AiContextToolWindow.updateQueue(project.adapt()) // Initial update
    }
}