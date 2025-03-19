package com.niloda.aicontext.intellij

import com.intellij.ui.table.JBTable
import java.awt.Component
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.DefaultCellEditor
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.table.DefaultTableModel

fun JBTable.applyEditor(queueModel: DefaultTableModel) {
    apply {
        setDefaultEditor(Any::class.java, object : DefaultCellEditor(JTextField()) {
            override fun getTableCellEditorComponent(
                table: JTable,
                value: Any?,
                isSelected: Boolean,
                row: Int,
                column: Int
            ): Component {
                if (this@apply.isRowGroupHeader(row)) {
                    return super.getTableCellEditorComponent(table, value, isSelected, row, column)
                }
                val item = QueueManager.aiService.queue.elementAtOrNull(row - groupedRowOffset(row))
                    ?: return super.getTableCellEditorComponent(table, value, isSelected, row, column)
                return when (column) {
                    1 -> { // Prompt column
                        val textField = JTextField(item.prompt)
                        textField.addActionListener {
                            item.prompt = textField.text
                            queueModel.setValueAt(item.prompt, row, column)
                            queueModel.fireTableCellUpdated(row, column)
                        }
                        textField.addFocusListener(object : FocusAdapter() {
                            override fun focusLost(e: FocusEvent?) {
                                item.prompt = textField.text
                                queueModel.setValueAt(item.prompt, row, column)
                                queueModel.fireTableCellUpdated(row, column)
                                stopCellEditing()
                            }
                        })
                        textField
                    }

                    2 -> { // Output Destination column
                        val textField = JTextField(item.outputDestination)
                        textField.addActionListener {
                            item.outputDestination = textField.text
                            queueModel.setValueAt(item.outputDestination, row, column)
                            queueModel.fireTableCellUpdated(row, column)
                        }
                        textField.addFocusListener(object : FocusAdapter() {
                            override fun focusLost(e: FocusEvent?) {
                                item.outputDestination = textField.text
                                queueModel.setValueAt(item.outputDestination, row, column)
                                queueModel.fireTableCellUpdated(row, column)
                                stopCellEditing()
                            }
                        })
                        textField
                    }

                    else -> super.getTableCellEditorComponent(table, value, isSelected, row, column)
                }
            }

            override fun stopCellEditing(): Boolean {
                val success = super.stopCellEditing()
                if (success && !this@apply.isRowGroupHeader(editingRow)) {
                    val item = QueueManager.aiService.queue.elementAtOrNull(editingRow - groupedRowOffset(editingRow))
                    if (item != null) {
                        val newValue = getCellEditorValue() as String
                        when (editingColumn) {
                            1 -> item.prompt = newValue
                            2 -> item.outputDestination = newValue
                        }
                        queueModel.setValueAt(newValue, editingRow, editingColumn)
                        queueModel.fireTableCellUpdated(editingRow, editingColumn)
                    }
                }
                return success
            }
        })
    }
}

private fun JBTable.groupedRowOffset(row: Int): Int {
    val groupedItems = QueueManager.aiService.queue.groupBy { it.groupName }
    var currentRow = 0
    groupedItems.forEach { (_, items) ->
        if (row in currentRow until (currentRow + 1 + items.size)) return currentRow
        currentRow += 1 + items.size
    }
    return 0
}