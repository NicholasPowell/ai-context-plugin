package com.niloda.aicontext

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.niloda.aicontext.AiContextQueueManager.aiService
import com.niloda.aicontext.impl.adapt
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import java.io.File
import javax.swing.table.DefaultTableModel

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
        queueModel.addRow(arrayOf(item.getDisplayPath(project), item.prompt, item.outputDestination, "Run", item.status.toString(), item.getElapsedTime()))
        queueModel.addRow(arrayOf(item.result ?: "", "", "", "Save", "", ""))
        println("Added to queue UI: ${item.file.name}, total rows: ${queueModel.rowCount}")
        queueTable.repaint()
    }

    fun updateQueue(project: IProject) {
        queueModel.rowCount = 0
        aiService.queue.forEach { item ->
            queueModel.addRow(arrayOf(item.getDisplayPath(project), item.prompt, item.outputDestination,
                when (item.status) {
                    QueueItem.Status.PENDING -> "Run"
                    QueueItem.Status.RUNNING -> "Cancel"
                    else -> "Run"
                },
                item.status.toString(), item.getElapsedTime()))
            queueModel.addRow(arrayOf(item.result ?: "", "", "", "Save", "", ""))
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
            queueModel.setValueAt("Save", rowIndex + 1, 3) // Ensure Save button is present
            for (col in listOf(1, 2, 4, 5)) {
                queueModel.setValueAt("", rowIndex + 1, col)
            }
            queueModel.fireTableDataChanged() // Ensure table updates
            println("Result set in table at row ${rowIndex + 1}, total rows: ${queueModel.rowCount}")
        } else {
            println("Failed to set result: rowIndex $rowIndex out of bounds, queue size: ${aiService.queue.size}, table rows: ${queueModel.rowCount}")
            AiContextToolWindow.updateQueue(project) // Force a full refresh as fallback
        }
    }

    fun saveResult(item: QueueItem, project: IProject) {
        if (item.result == null || item.outputDestination.isBlank()) {
            println("Cannot save: No result or output destination for ${item.file.name}")
            return
        }
        try {
            val outputFile = File(project.basePath +"/" + item.outputDestination)
            outputFile.parentFile?.mkdirs() // Create directories if they don't exist
            outputFile.writeText(item.result!!)
            println("Saved result to ${item.outputDestination} for ${item.file.name}")
        } catch (e: Exception) {
            println("Failed to save result to ${item.outputDestination}: ${e.message}")
        }
    }
}