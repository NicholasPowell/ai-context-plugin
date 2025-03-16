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

        val queueModel = object : DefaultTableModel(arrayOf("File", "Prompt", "Action", "Status", "Time"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                val isFileRow = row % 2 == 0
                return isFileRow && column == 1 // Only "Prompt" in file rows is editable
            }
        }
        val queueTable = JBTable(queueModel)
        queueTable.apply {
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            columnModel.getColumn(0).preferredWidth = 300
            columnModel.getColumn(1).preferredWidth = 400
            columnModel.getColumn(2).maxWidth = 80
            columnModel.getColumn(3).maxWidth = 100
            columnModel.getColumn(4).maxWidth = 80
            rowHeight = 25 // Default height for file rows

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    val row = queueTable.rowAtPoint(e.point)
                    val col = queueTable.columnAtPoint(e.point)
                    if (row % 2 != 0 || col != 2) return // Only handle "Action" column in file rows
                    val item = aiService.queue.elementAtOrNull(row / 2)
                    if (item != null) {
                        when (item.status) {
                            QueueItem.Status.PENDING -> {
                                println("Run clicked for ${item.file.name}")
                                AiContextQueueManager.processFile(item, project.adapt())
                            }
                            QueueItem.Status.RUNNING -> {
                                println("Cancel clicked for ${item.file.name}")
                                AiContextQueueManager.terminate(item.file)
                                queueModel.setValueAt("Run", row, 2)
                                queueModel.setValueAt("CANCELLED", row, 3)
                                queueTable.repaint()
                            }
                            else -> {
                                println("No action for status ${item.status} on ${item.file.name}")
                            }
                        }
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
                        isFileRow && column == 2 -> { // Action column in file row
                            val button = when (item.status) {
                                QueueItem.Status.PENDING -> JButton("Run")
                                QueueItem.Status.RUNNING -> JButton("Cancel")
                                else -> JButton("Run").apply { isEnabled = false }
                            }
                            button.isEnabled = item.status == QueueItem.Status.PENDING || item.status == QueueItem.Status.RUNNING
                            button
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
                        else -> super.getTableCellEditorComponent(table, value, isSelected, row, column)
                    }
                }

                override fun stopCellEditing(): Boolean {
                    val success = super.stopCellEditing()
                    if (success) {
                        val row = editingRow
                        if (row >= 0 && row % 2 == 0 && editingColumn == 1) {
                            val item = aiService.queue.elementAtOrNull(row / 2)
                            item?.prompt = (getCellEditorValue() as String)
                            queueModel.setValueAt(item?.prompt, row, 1)
                            queueTable.repaint()
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

object AiContextToolWindow {
    private lateinit var queueModel: DefaultTableModel
    private lateinit var queueTable: JBTable
    private lateinit var project: Project

    fun init(model: DefaultTableModel, table: JBTable, proj: Project) {
        queueModel = model
        queueTable = table
        project = proj
    }

    fun addToQueue(item: QueueItem, project: IProject) {
        val existingRow = (0 until queueModel.rowCount step 2).find { row ->
            queueModel.getValueAt(row, 0) == item.getDisplayPath(project)
        }
        if (existingRow != null) {
            println("Replacing UI row for ${item.file.name} at row $existingRow")
            queueModel.removeRow(existingRow + 1) // Remove results row
            queueModel.removeRow(existingRow) // Remove file row
        }
        queueModel.addRow(arrayOf(item.getDisplayPath(project), item.prompt, "Run", item.status.toString(), item.getElapsedTime()))
        queueModel.addRow(arrayOf(item.result ?: "", "", "", "", "")) // Use stored result
        println("Added to queue UI: ${item.file.name}, total rows: ${queueModel.rowCount}")
        queueTable.repaint()
    }

    fun updateQueue(project: IProject) {
        queueModel.rowCount = 0
        aiService.queue.forEach { item ->
            queueModel.addRow(arrayOf(item.getDisplayPath(project), item.prompt,
                when (item.status) {
                    QueueItem.Status.PENDING -> "Run"
                    QueueItem.Status.RUNNING -> "Cancel"
                    else -> "Run"
                },
                item.status.toString(), item.getElapsedTime()))
            queueModel.addRow(arrayOf(item.result ?: "", "", "", "", ""))
        }
        queueModel.fireTableDataChanged() // Force full refresh
        println("Updated queue UI, rows: ${queueModel.rowCount}")
    }

    fun setResult(item: QueueItem, project: IProject, result: String?) {
        item.result = "Result: ${result ?: "Error: Failed to process file"}" // Store result in item
        val queueIndex = aiService.queue.indexOf(item)
        val rowIndex = queueIndex * 2
        println("Setting result for ${item.file.name}, queue index: $queueIndex, row: ${rowIndex + 1}, result: ${result?.take(50) ?: "null"}")
        if (rowIndex >= 0 && rowIndex + 1 < queueModel.rowCount) {
            queueModel.setValueAt(item.result, rowIndex + 1, 0)
            for (col in 1 until queueModel.columnCount) {
                queueModel.setValueAt("", rowIndex + 1, col)
            }
            queueModel.fireTableDataChanged() // Ensure table updates
            println("Result set in table at row ${rowIndex + 1}, total rows: ${queueModel.rowCount}")
        } else {
            println("Failed to set result: rowIndex $rowIndex out of bounds, queue size: ${aiService.queue.size}, table rows: ${queueModel.rowCount}")
            AiContextToolWindow.updateQueue(project) // Force a full refresh as fallback
        }
    }
}