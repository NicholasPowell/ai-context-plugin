package com.niloda.aicontext.intellij

import com.intellij.ui.table.JBTable
import com.niloda.aicontext.model.QueueItem
import java.awt.Component
import javax.swing.JButton
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

fun JBTable.applyRenderer() {
    apply {
        setDefaultRenderer(Any::class.java, object : DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
            ): Component {
                if (rowIsGroupHeader(row)) {
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).apply {
                        font = font.deriveFont(java.awt.Font.BOLD)
                    }
                }

                val item = getItemAtRow(row)
                    ?: return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)

                return when (column) {
                    3 -> { // Action column
                        val button = when (item.status) {
                            QueueItem.Status.PENDING -> JButton("Run")
                            QueueItem.Status.RUNNING -> JButton("Cancel")
                            else -> JButton("Save").apply {
                                isEnabled = item.result != null && item.outputDestination.isNotBlank()
                            }
                        }
                        button.isEnabled = when (item.status) {
                            QueueItem.Status.PENDING -> true
                            QueueItem.Status.RUNNING -> true
                            QueueItem.Status.DONE -> item.result != null && item.outputDestination.isNotBlank()
                            else -> false // ERROR or CANCELLED
                        }
                        button
                    }
                    else -> super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                }
            }
        })
    }
}

private fun JBTable.rowIsGroupHeader(row: Int): Boolean {
    val value = model.getValueAt(row, 0)?.toString() ?: return false
    return value.startsWith("Group: ")
}

private fun JBTable.getItemAtRow(row: Int): QueueItem? {
    val groupedItems = QueueManager.aiService.queue.groupBy { it.groupName }
    var currentRow = 0
    groupedItems.forEach { (_, items) ->
        currentRow++ // Skip group header
        items.forEachIndexed { index, item ->
            val itemRow = currentRow + index
            if (row == itemRow) return item
        }
        currentRow += items.size
    }
    return null
}