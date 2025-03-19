package com.niloda.aicontext.intellij

import com.intellij.openapi.project.Project
import com.intellij.ui.table.JBTable
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import java.io.File
import javax.swing.Timer
import javax.swing.table.DefaultTableModel

object AiProcessorToolWindow {
    private lateinit var queueModel: DefaultTableModel
    private lateinit var queueTable: JBTable
    private lateinit var project: Project
    private var updateTimer: Timer? = null

    fun init(model: DefaultTableModel, table: JBTable, proj: Project) {
        queueModel = model
        queueTable = table
        project = proj
    }

    fun addToQueue(item: QueueItem, project: IProject) {
        updateQueue(project)
    }

    fun updateQueue(project: IProject) {
        queueModel.rowCount = 0
        val groupedItems = QueueManager.aiService.queue.groupBy { it.groupName }
        groupedItems.forEach { (groupName, items) ->
            queueModel.addRow(arrayOf("Group: $groupName", "", "", "", "", ""))
            items.forEach { item ->
                queueModel.addRow(arrayOf(
                    item.getDisplayPath(project),
                    item.prompt,
                    item.outputDestination,
                    when (item.status) {
                        QueueItem.Status.PENDING -> "Run"
                        QueueItem.Status.RUNNING -> "Cancel"
                        else -> "Save"
                    },
                    item.status.toString(),
                    item.getElapsedTime()
                ))
            }
        }
        queueModel.fireTableDataChanged()
        println("Updated queue UI with groups, rows: ${queueModel.rowCount}")
        checkTimerState(project)
    }

    fun setResult(item: QueueItem, project: IProject, result: String?) {
        item.result = result ?: "Error: Failed to process file"
        updateSpecificRow(item, project)  // New method instead of full update
        checkTimerState(project)
    }

    private fun updateSpecificRow(item: QueueItem, project: IProject) {
        val groupedItems = QueueManager.aiService.queue.groupBy { it.groupName }
        var currentRow = 0

        for ((groupName, items) in groupedItems) {
            currentRow++ // Skip group header
            val itemIndex = items.indexOf(item)
            if (itemIndex != -1) {
                val row = currentRow + itemIndex
                queueModel.setValueAt(item.getDisplayPath(project), row, 0)
                queueModel.setValueAt(item.prompt, row, 1)
                queueModel.setValueAt(item.outputDestination, row, 2)
                queueModel.setValueAt(
                    when (item.status) {
                        QueueItem.Status.PENDING -> "Run"
                        QueueItem.Status.RUNNING -> "Cancel"
                        else -> "Save"
                    },
                    row, 3
                )
                queueModel.setValueAt(item.status.toString(), row, 4)
                queueModel.setValueAt(item.getElapsedTime(), row, 5)
                queueModel.fireTableRowsUpdated(row, row)
                queueTable.repaint()
                return
            }
            currentRow += items.size
        }
    }

    fun saveResult(item: QueueItem, project: IProject) {
        if (item.result == null || item.outputDestination.isBlank()) {
            println("Cannot save: No result or output destination for ${item.file.name}")
            return
        }
        try {
            val outputFile = File(project.basePath + "/" + item.outputDestination)
            outputFile.parentFile?.mkdirs()
            outputFile.writeText(item.result!!)
            println("Saved result to ${item.outputDestination} for ${item.file.name}")
        } catch (e: Exception) {
            println("Failed to save result to ${item.outputDestination}: ${e.message}")
        }
    }

    fun startTimer(project: IProject) {
        if (updateTimer?.isRunning == true) return
        updateTimer = Timer(1000) {
            var hasRunningTasks = false
            QueueManager.aiService.queue.forEachIndexed { index, item ->
                if (item.status == QueueItem.Status.RUNNING) {
                    hasRunningTasks = true
                    val groupOffset = groupedRowOffset(item.groupName)
                    val row = groupOffset + index + 1 // +1 for group header
                    queueModel.setValueAt(item.getElapsedTime(), row, 5)
                    queueModel.fireTableCellUpdated(row, 5)
                }
            }
            if (!hasRunningTasks) {
                updateTimer?.stop()
                updateTimer = null
                println("Timer stopped: no running tasks")
            }
            queueTable.repaint()
        }.apply {
            start()
            println("Timer started for updating elapsed time")
        }
    }

    private fun groupedRowOffset(groupName: String): Int {
        val groupedItems = QueueManager.aiService.queue.groupBy { it.groupName }
        var offset = 0
        for ((name, items) in groupedItems) {
            if (name == groupName) return offset
            offset += 1 + items.size // 1 for header, 1 row per item
        }
        return offset
    }

    private fun checkTimerState(project: IProject) {
        val hasRunningTasks = QueueManager.aiService.queue.any { it.status == QueueItem.Status.RUNNING }
        if (hasRunningTasks && updateTimer?.isRunning != true) {
            startTimer(project)
        } else if (!hasRunningTasks && updateTimer?.isRunning == true) {
            updateTimer?.stop()
            updateTimer = null
            println("Timer stopped: no running tasks after update")
        }
    }
}