package com.niloda.aicontext.intellij

import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import com.niloda.aicontext.model.QueueItem
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import javax.swing.JComponent
import javax.swing.TransferHandler
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

fun Tree.applyDragAndDrop(project: Project) {
    isEditable = false
    dragEnabled = true // Enable drag support
    isFocusable = true // Ensure focus for events

    transferHandler = object : TransferHandler() {
        override fun createTransferable(c: JComponent?): Transferable? {
            val tree = c as? Tree ?: return null
            val path = tree.selectionPath ?: return null
            val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return null
            val item = node.userObject as? QueueItem ?: return null
            println("Creating transferable for item: ${item.file.name}")
            return object : Transferable {
                override fun getTransferDataFlavors() = arrayOf(DataFlavor.stringFlavor)
                override fun isDataFlavorSupported(flavor: DataFlavor?) = flavor == DataFlavor.stringFlavor
                override fun getTransferData(flavor: DataFlavor?): Any {
                    println("Transferring data: ${item.file.virtualFilePath}")
                    return item.file.virtualFilePath
                }
            }
        }

        override fun getSourceActions(c: JComponent?): Int {
            println("Getting source actions for drag")
            return MOVE // Specify MOVE action
        }

        override fun canImport(info: TransferSupport): Boolean {
            val tree = info.component as? Tree ?: return false
            if (!info.isDrop || !info.isDataFlavorSupported(DataFlavor.stringFlavor)) return false
            val dropLocation = info.dropLocation as? javax.swing.JTree.DropLocation ?: return false
            val dropPath = dropLocation.path ?: return false
            val dropNode = dropPath.lastPathComponent as? DefaultMutableTreeNode ?: return false
            val canImport = dropNode.userObject is String
            println("Can import to ${dropNode.userObject}? $canImport")
            return canImport // Can only drop onto group nodes
        }

        override fun importData(info: TransferSupport): Boolean {
            val tree = info.component as? Tree ?: return false
            val dropLocation = info.dropLocation as? javax.swing.JTree.DropLocation ?: return false
            val dropPath = dropLocation.path ?: return false
            val dropNode = dropPath.lastPathComponent as? DefaultMutableTreeNode ?: return false
            if (dropNode.userObject !is String) return false

            val transferable = info.transferable
            val filePath = transferable.getTransferData(DataFlavor.stringFlavor) as? String ?: return false
            val sourceItem = QueueManager.aiService.queue.find { it.file.virtualFilePath == filePath } ?: return false

            val targetGroup = dropNode.userObject.toString().removePrefix("Group: ")
            println("Moving ${sourceItem.file.name} to group: $targetGroup")
            IntelliJAiFileProcessor.moveItemToGroup(sourceItem, targetGroup)
            return true
        }
    }

    // Ensure selection on click
    addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            val path = getPathForLocation(e.x, e.y)
            if (path != null) {
                selectionPath = path
                println("Selected path: ${path.lastPathComponent}")
            }
        }
    })

    // Start drag on motion
    addMouseMotionListener(object : MouseMotionAdapter() {
        override fun mouseDragged(e: MouseEvent) {
            val path = getPathForLocation(e.x, e.y) ?: return
            val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return
            if (node.userObject is QueueItem) {
                println("Starting drag for ${node.userObject}")
                val handler = transferHandler
                try {
                    handler.exportAsDrag(this@applyDragAndDrop, e, TransferHandler.MOVE)
                    println("Drag exported successfully")
                } catch (ex: Exception) {
                    println("Failed to export drag: ${ex.message}")
                }
            }
        }
    })
}