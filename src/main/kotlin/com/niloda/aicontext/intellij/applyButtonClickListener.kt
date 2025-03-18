// === File: src/main/kotlin/com/niloda/aicontext/intellij/applyButtonClickListener.kt
package com.niloda.aicontext.intellij

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.table.JBTable
import com.niloda.aicontext.model.QueueItem
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.table.DefaultTableModel

fun JBTable.applyButtonClickListener(
    project: Project,
    queueModel: DefaultTableModel
) {
    apply {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val row = rowAtPoint(e.point)
                val col = columnAtPoint(e.point)
                val isFileRow = row % 2 == 0
                val item = QueueManager.aiService.queue.elementAtOrNull(row / 2) ?: return

                println("Mouse clicked: row=$row, col=$col, clickCount=${e.clickCount}, isFileRow=$isFileRow")

                if (isFileRow && col == 3 && e.clickCount == 1) { // Action column in file row, single-click
                    when (item.status) {
                        QueueItem.Status.PENDING,
                        QueueItem.Status.DONE,
                        QueueItem.Status.ERROR,
                        QueueItem.Status.CANCELLED -> {
                            println("Run initiated for ${item.file.name} (status: ${item.status})")
                            if (item.status != QueueItem.Status.PENDING) {
                                item.status = QueueItem.Status.PENDING
                                item.startTime = null
                                item.result = null
                                queueModel.setValueAt("", row + 1, 0) // Clear result in UI
                                queueModel.fireTableCellUpdated(row + 1, 0)
                            }
                            QueueManager.processFile(item, project.adapt())
                            queueModel.setValueAt("Cancel", row, 3)
                            queueModel.setValueAt("RUNNING", row, 4)
                            queueModel.setValueAt(item.getElapsedTime(), row, 5) // Initial time
                            queueModel.fireTableCellUpdated(row, 3)
                            queueModel.fireTableCellUpdated(row, 4)
                            queueModel.fireTableCellUpdated(row, 5)
                            AiProcessorToolWindow.startTimer(project.adapt()) // Start timer
                            repaint()
                        }
                        QueueItem.Status.RUNNING -> {
                            println("Cancel initiated for ${item.file.name}")
                            QueueManager.terminate(item.file)
                            queueModel.setValueAt("Run", row, 3)
                            queueModel.setValueAt("CANCELLED", row, 4)
                            queueModel.setValueAt("-", row, 5)
                            queueModel.fireTableCellUpdated(row, 3)
                            queueModel.fireTableCellUpdated(row, 4)
                            queueModel.fireTableCellUpdated(row, 5)
                            repaint()
                        }
                    }
                } else if (!isFileRow && col == 3 && e.clickCount == 1) { // Save button in results row
                    println("Save initiated for ${item.file.name}")
                    AiProcessorToolWindow.saveResult(item, project.adapt())
                } else if (!isFileRow && col == 0 && e.clickCount == 2) { // Double-click on result cell
                    println("Double-clicked result for ${item.file.name}")
                    item.result?.let { result ->
                        openResultInEditor(project, item.file.name, result)
                    } ?: println("No result available to display for ${item.file.name}")
                }
            }
        })
    }
}

private fun openResultInEditor(project: Project, fileName: String, result: String) {
    val fileEditorManager = FileEditorManager.getInstance(project)
    val virtualFileName = "AI_Result_${fileName}_output.txt"

    // Check if a file with the same name is already open
    val existingFile = fileEditorManager.openFiles.find { it.name == virtualFileName }
    if (existingFile != null) {
        val editors = fileEditorManager.getEditors(existingFile)
        println("Found ${editors.size} editors for $virtualFileName")
        val textEditor = editors.filterIsInstance<TextEditor>().firstOrNull()
        if (textEditor != null) {
            val document = textEditor.editor.document
            println("Current document content length: ${document.textLength}, new result length: ${result.length}")
            ApplicationManager.getApplication().invokeAndWait {
                CommandProcessor.getInstance().executeCommand(project, {
                    document.setText(result)
                    println("Document content updated via CommandProcessor for $virtualFileName")
                }, "Update AI Result", null)
            }
            fileEditorManager.openFile(existingFile, true) // Ensure it’s in focus
            println("Updated and focused existing editor for $virtualFileName")
            return
        } else {
            println("No TextEditor found for $virtualFileName, closing existing file")
            fileEditorManager.closeFile(existingFile)
        }
    }

    // Create and open a new virtual file if none exists or if the existing one was closed
    val virtualFile = LightVirtualFile(virtualFileName, result).apply {
        isWritable = false
    }
    fileEditorManager.openFile(virtualFile, true)
    println("Opened new editor for $virtualFileName")
}