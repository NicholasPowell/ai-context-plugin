package com.niloda.aicontext.intellij

import com.intellij.openapi.project.Project
import com.intellij.ui.table.JBTable
import com.niloda.aicontext.model.QueueItem
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.TransferHandler

fun JBTable.applyDragAndDrop(project: Project) {
    transferHandler = object : TransferHandler() {
        override fun createTransferable(c: JComponent?): Transferable? {
            val table = c as? JBTable ?: return null
            val row = table.selectedRow
            if (row < 0 || table.rowIsGroupHeader(row)) return null

            val item = table.getItemAtRow(row) ?: return null
            return object : Transferable {
                override fun getTransferDataFlavors() = arrayOf(DataFlavor.stringFlavor)
                override fun isDataFlavorSupported(flavor: DataFlavor?) = flavor == DataFlavor.stringFlavor
                override fun getTransferData(flavor: DataFlavor?): Any {
                    return item.file.virtualFilePath
                }
            }
        }

        override fun canImport(info: TransferSupport): Boolean {
            val table = info.component as? JBTable ?: return false
            if (!info.isDrop || !info.isDataFlavorSupported(DataFlavor.stringFlavor)) return false
            val dropRow = (info.dropLocation as? JTable.DropLocation)?.row ?: return false
            return !table.rowIsGroupHeader(dropRow)
        }

        override fun importData(info: TransferSupport): Boolean {
            val table = info.component as? JBTable ?: return false
            val dropRow = (info.dropLocation as? JTable.DropLocation)?.row ?: return false
            if (table.rowIsGroupHeader(dropRow)) return false

            val transferable = info.transferable
            val filePath = transferable.getTransferData(DataFlavor.stringFlavor) as? String ?: return false
            val sourceItem = QueueManager.aiService.queue.find { it.file.virtualFilePath == filePath } ?: return false
            val targetItem = table.getItemAtRow(dropRow) ?: return false

            val targetGroup = targetItem.groupName
            IntelliJAiFileProcessor.moveItemToGroup(sourceItem, targetGroup)
            AiProcessorToolWindow.updateQueue(project.adapt())
            return true
        }
    }
    setDragEnabled(true)
    dropMode = javax.swing.DropMode.ON
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