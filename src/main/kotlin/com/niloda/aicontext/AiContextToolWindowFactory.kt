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
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Timer
import java.util.TimerTask
import javax.swing.DefaultCellEditor
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class AiContextToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Initializing tool window for project: ${project.name}")
        val panel = JPanel(BorderLayout())

        val queueModel = object : DefaultTableModel(arrayOf("File", "Prompt", "Action", "Status", "Time"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = column == 1
        }
        val queueTable = JBTable(queueModel)
        queueTable.apply {
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            columnModel.getColumn(0).preferredWidth = 300
            columnModel.getColumn(1).preferredWidth = 200
            columnModel.getColumn(2).maxWidth = 80
            columnModel.getColumn(3).maxWidth = 100
            columnModel.getColumn(4).maxWidth = 80

            addMouseListener(object: MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    val row = queueTable.rowAtPoint(e.point)
                    val col = queueTable.columnAtPoint(e.point)
                    val item = aiService.queue.elementAtOrNull(row)
                    if(item != null && col == 2) {
                        if (item.status == AiContextService.QueueItem.Status.PENDING) {
                            AiContextQueueManager.processFile(item, project.adapt())
                        } else if (item.status == AiContextService.QueueItem.Status.RUNNING) {
                            AiContextQueueManager.terminate(item.file)
                        }
                    }
                }
            })


            setDefaultRenderer(Any::class.java, object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
                ): java.awt.Component {
                    val item = aiService.queue.elementAtOrNull(row) ?: return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    return when (column) {
                        2 -> {
                            val button = if (item.status == AiContextService.QueueItem.Status.PENDING) JButton("Run") else JButton("Cancel")
                            button.isEnabled = true
                            button
                        }
                        else -> super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    }
                }
            })

            setDefaultEditor(Any::class.java, object : DefaultCellEditor(JTextField()) {
                override fun getTableCellEditorComponent(table: JTable, value: Any?, isSelected: Boolean, row: Int, column: Int): java.awt.Component {
                    val item = aiService.queue.elementAtOrNull(row) ?: return super.getTableCellEditorComponent(table, value, isSelected, row, column)
                    return when (column) {
                        1 -> {
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
                        if (row >= 0 && editingColumn == 1) {
                            val item = aiService.queue.elementAtOrNull(row)
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
                    AiContextToolWindow.updateQueue(project.adapt())
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

    fun addToQueue(item: AiContextService.QueueItem, project: IProject) {
        queueModel.addRow(arrayOf(item.getDisplayPath(project), item.prompt, "Run", item.status.toString(), item.getElapsedTime()))
        println("Added to queue UI: ${item.file.name}")
    }

    fun updateQueue(project: IProject) {
        queueModel.rowCount = 0
        aiService.queue.forEach { item ->
            queueModel.addRow(arrayOf(item.getDisplayPath(project), item.prompt,
                if (item.status == AiContextService.QueueItem.Status.PENDING) "Run" else "Cancel",
                item.status.toString(), item.getElapsedTime()))
        }
        queueTable.repaint()
        println("Updated queue UI, rows: ${queueModel.rowCount}")
    }

    fun appendOutput(text: String) {
        outputArea.append("$text\n")
        outputArea.caretPosition = outputArea.document.length
    }

    fun clearOutput() {
        outputArea.text = ""
    }
}