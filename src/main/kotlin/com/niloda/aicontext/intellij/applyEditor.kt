package com.niloda.aicontext.intellij

import com.intellij.ui.table.JBTable
import java.awt.Component
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
                val item = QueueManager.aiService.queue.elementAtOrNull(row / 2)
                    ?: return super.getTableCellEditorComponent(table, value, isSelected, row, column)
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
                        val item = QueueManager.aiService.queue.elementAtOrNull(row / 2)
                        if (item != null) {
                            when (editingColumn) {
                                1 -> item.prompt = (getCellEditorValue() as String)
                                2 -> item.outputDestination = (getCellEditorValue() as String)
                            }
                            queueModel.setValueAt(
                                if (editingColumn == 1) item.prompt else item.outputDestination,
                                row,
                                editingColumn
                            )
                            repaint()
                        }
                    }
                }
                return success
            }
        })
    }
}