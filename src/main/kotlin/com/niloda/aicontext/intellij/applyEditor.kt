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
                val item = QueueManager.aiService.queue.elementAtOrNull(row / 2)
                    ?: return super.getTableCellEditorComponent(table, value, isSelected, row, column)
                return when {
                    column == 1 && row % 2 == 0 -> { // Prompt in file row
                        val textField = JTextField(item.prompt)
                        textField.addActionListener {
                            item.prompt = textField.text
                            println("Prompt updated via Enter: ${item.prompt}")
                            queueModel.setValueAt(item.prompt, row, column)
                            queueModel.fireTableCellUpdated(row, column)
                        }
                        textField.addFocusListener(object : FocusAdapter() {
                            override fun focusLost(e: FocusEvent?) {
                                item.prompt = textField.text
                                println("Prompt updated via focus loss: ${item.prompt}")
                                queueModel.setValueAt(item.prompt, row, column)
                                queueModel.fireTableCellUpdated(row, column)
                                stopCellEditing() // Ensure editing stops cleanly
                            }
                        })
                        textField
                    }
                    column == 2 && row % 2 == 0 -> { // Output Destination in file row
                        val textField = JTextField(item.outputDestination)
                        textField.addActionListener {
                            item.outputDestination = textField.text
                            println("Output destination updated via Enter: ${item.outputDestination}")
                            queueModel.setValueAt(item.outputDestination, row, column)
                            queueModel.fireTableCellUpdated(row, column)
                        }
                        textField.addFocusListener(object : FocusAdapter() {
                            override fun focusLost(e: FocusEvent?) {
                                item.outputDestination = textField.text
                                println("Output destination updated via focus loss: ${item.outputDestination}")
                                queueModel.setValueAt(item.outputDestination, row, column)
                                queueModel.fireTableCellUpdated(row, column)
                                stopCellEditing() // Ensure editing stops cleanly
                            }
                        })
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
                            val newValue = getCellEditorValue() as String
                            when (editingColumn) {
                                1 -> {
                                    item.prompt = newValue
                                    println("Prompt set in stopCellEditing: $newValue at row $row")
                                }
                                2 -> {
                                    item.outputDestination = newValue
                                    println("Output destination set in stopCellEditing: $newValue at row $row")
                                }
                            }
                            queueModel.setValueAt(newValue, row, editingColumn)
                            println("Model updated at ($row, $editingColumn) with: $newValue")
                            queueModel.fireTableCellUpdated(row, editingColumn)
                            println("Fired table cell update for ($row, $editingColumn)")
                        }
                    }
                }
                return success
            }
        })
    }
}