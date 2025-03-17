package com.niloda.aicontext.intellij

import com.intellij.openapi.project.Project
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

                if (isFileRow && col == 3) { // Action column in file row
                    when (item.status) {
                        QueueItem.Status.PENDING -> {
                            println("Run clicked for ${item.file.name}")
                            QueueManager.processFile(item, project.adapt())
                        }

                        QueueItem.Status.RUNNING -> {
                            println("Cancel clicked for ${item.file.name}")
                            QueueManager.terminate(item.file)
                            queueModel.setValueAt("Run", row, 3)
                            queueModel.setValueAt("CANCELLED", row, 4)
                            repaint()
                        }

                        else -> {
                            println("No action for status ${item.status} on ${item.file.name}")
                        }
                    }
                } else if (!isFileRow && col == 3) { // Save button in results row
                    println("Save clicked for ${item.file.name}")
                    AiProcessorToolWindow.saveResult(item, project.adapt())
                }
            }
        })
    }
}