package com.niloda.aicontext.intellij

import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.tree.*

object AiProcessorToolWindow {
    private lateinit var queueTree: Tree
    private lateinit var treeModel: DefaultTreeModel
    private lateinit var project: Project
    private var updateTimer: Timer? = null

    fun init(tree: Tree, proj: Project) {
        queueTree = tree
        project = proj
        treeModel = DefaultTreeModel(DefaultMutableTreeNode("Queue"))
        queueTree.model = treeModel
        queueTree.isRootVisible = false
        queueTree.cellRenderer = QueueTreeCellRenderer(treeModel)
        queueTree.cellEditor = QueueTreeCellEditor(treeModel)
        queueTree.isEditable = true
        queueTree.rowHeight = 30 // Fixed row height
        queueTree.addMouseListener(QueueTreeMouseListener(project))
        queueTree.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val path = queueTree.getPathForLocation(e.x, e.y) ?: return
                val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return
                val item = node.userObject as? QueueItem ?: return
                val bounds = queueTree.getPathBounds(path) ?: return
                val x = e.x - bounds.x
                val runButtonX = 434 // Approx start of button (150 + 150 + 100 + 80 + 50 + insets)
                val runButtonEndX = runButtonX + 24 // Button width
                if (x in runButtonX..runButtonEndX && item.status == QueueItem.Status.PENDING) {
                    println("Run button area clicked for ${item.file.name} at x=$x")
                    QueueManager.processFile(item, project.adapt())
                    updateQueue(project.adapt())
                    startTimer(project.adapt())
                }
            }
        })
        println("Tree initialized with fixed row height: 30 and custom Run button listener")
    }

    fun addToQueue(item: QueueItem, project: IProject) {
        updateQueue(project)
    }

    fun updateQueue(project: IProject) {
        val root = treeModel.root as DefaultMutableTreeNode
        root.removeAllChildren()
        val groupedItems = QueueManager.aiService.queue.groupBy { it.groupName }
        groupedItems.forEach { (groupName, items) ->
            val groupNode = DefaultMutableTreeNode("Group: $groupName")
            items.forEach { item ->
                groupNode.add(DefaultMutableTreeNode(item))
            }
            root.add(groupNode)
        }
        treeModel.reload()
        queueTree.expandRow(0)
        println("Updated queue UI with groups, items: ${QueueManager.aiService.queue.size}")
        checkTimerState(project)
    }

    fun setResult(item: QueueItem, project: IProject, result: String?) {
        item.result = result ?: "Error: Failed to process file"
        updateSpecificItem(item, project)
        checkTimerState(project)
    }

    private fun updateSpecificItem(item: QueueItem, project: IProject) {
        val root = treeModel.root as DefaultMutableTreeNode
        for (i in 0 until root.childCount) {
            val groupNode = root.getChildAt(i) as DefaultMutableTreeNode
            val groupName = groupNode.userObject.toString().removePrefix("Group: ")
            if (groupName == item.groupName) {
                for (j in 0 until groupNode.childCount) {
                    val itemNode = groupNode.getChildAt(j) as DefaultMutableTreeNode
                    if (itemNode.userObject == item) {
                        treeModel.nodeChanged(itemNode)
                        return
                    }
                }
            }
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
            QueueManager.aiService.queue.forEach { item ->
                if (item.status == QueueItem.Status.RUNNING) {
                    hasRunningTasks = true
                    updateSpecificItem(item, project)
                }
            }
            if (!hasRunningTasks) {
                updateTimer?.stop()
                updateTimer = null
                println("Timer stopped: no running tasks")
            }
        }.apply {
            start()
            println("Timer started for updating elapsed time")
        }
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

