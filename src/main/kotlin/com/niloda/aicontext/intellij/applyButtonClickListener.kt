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
                if (isRowGroupHeader(row)) return

                val item = getItemAtRow(row) ?: return
                println("Mouse clicked: row=$row, col=$col, clickCount=${e.clickCount}")

                if (col == 3 && e.clickCount == 1) { // Action column
                    when (item.status) {
                        QueueItem.Status.PENDING -> {
                            println("Run initiated for ${item.file.name}")
                            QueueManager.processFile(item, project.adapt())
                            queueModel.setValueAt("Cancel", row, 3)
                            queueModel.setValueAt("RUNNING", row, 4)
                            queueModel.setValueAt(item.getElapsedTime(), row, 5)
                            queueModel.fireTableCellUpdated(row, 3)
                            queueModel.fireTableCellUpdated(row, 4)
                            queueModel.fireTableCellUpdated(row, 5)
                            AiProcessorToolWindow.startTimer(project.adapt())
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
                        QueueItem.Status.DONE -> {
                            if (item.result != null && item.outputDestination.isNotBlank()) {
                                println("Save initiated for ${item.file.name}")
                                AiProcessorToolWindow.saveResult(item, project.adapt())
                            } else {
                                println("Save not possible: missing result or output destination for ${item.file.name}")
                            }
                        }
                        else -> {
                            println("No action for status ${item.status} on ${item.file.name}")
                        }
                    }
                } else if (col == 0 && e.clickCount == 2) { // Double-click on file path to show result
                    println("Double-clicked file path for ${item.file.name}")
                    item.result?.let { result ->
                        openResultInEditor(project, item.file.name, result)
                    } ?: println("No result available to display for ${item.file.name}")
                }
            }
        })
    }
}

private fun JBTable.getItemAtRow(row: Int): QueueItem? = GetItemAtRow(row)

private fun openResultInEditor(project: Project, fileName: String, result: String) {
    val fileEditorManager = FileEditorManager.getInstance(project)
    val virtualFileName = "AI_Result_${fileName}_output.txt"

    val existingFile = fileEditorManager.openFiles.find { it.name == virtualFileName }
    if (existingFile != null) {
        val editors = fileEditorManager.getEditors(existingFile)
        val textEditor = editors.filterIsInstance<TextEditor>().firstOrNull()
        if (textEditor != null) {
            val document = textEditor.editor.document
            ApplicationManager.getApplication().invokeAndWait {
                CommandProcessor.getInstance().executeCommand(project, {
                    document.setText(result)
                }, "Update AI Result", null)
            }
            fileEditorManager.openFile(existingFile, true)
            return
        } else {
            fileEditorManager.closeFile(existingFile)
        }
    }

    val virtualFile = LightVirtualFile(virtualFileName, result).apply {
        isWritable = false
    }
    fileEditorManager.openFile(virtualFile, true)
}