package com.niloda.aicontext.intellij

import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.niloda.aicontext.model.QueueItem
import java.awt.Component
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JTable
import javax.swing.JTextArea
import javax.swing.table.DefaultTableCellRenderer

fun JBTable.applyRenderer() {
    apply {
        setDefaultRenderer(Any::class.java, object : DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
            ): Component {
                val isFileRow = row % 2 == 0
                val item = QueueManager.aiService.queue.elementAtOrNull(row / 2)
                    ?: return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)

                return when {
                    !isFileRow && column == 0 -> {
                        val textArea = JTextArea(value?.toString() ?: "").apply {
                            isEditable = false
                            lineWrap = true
                            wrapStyleWord = true
                        }
                        val scrollPane = JBScrollPane(textArea).apply {
                            preferredSize = Dimension(table.width, 100)
                            minimumSize = Dimension(table.width, 50)
                        }
                        table.setRowHeight(row, 100)
                        scrollPane
                    }
                    isFileRow && column == 1 -> {
                        super.getTableCellRendererComponent(table, item.prompt, isSelected, hasFocus, row, column)
                    }
                    isFileRow && column == 3 -> {
                        val button = when (item.status) {
                            QueueItem.Status.PENDING -> JButton("Run")
                            QueueItem.Status.RUNNING -> JButton("Cancel")
                            else -> JButton("Run").apply { isEnabled = false }
                        }
                        button.isEnabled = item.status == QueueItem.Status.PENDING || item.status == QueueItem.Status.RUNNING
                        button
                    }
                    !isFileRow && column == 3 -> {
                        JButton("Save").apply {
                            isEnabled = item.result != null && item.status == QueueItem.Status.DONE && item.outputDestination.isNotBlank()
                        }
                    }
                    else -> super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                }
            }
        })
    }
}