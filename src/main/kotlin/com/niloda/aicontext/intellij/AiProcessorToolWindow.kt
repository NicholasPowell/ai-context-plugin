package com.niloda.aicontext.intellij

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.treeStructure.Tree
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.tree.*
import java.awt.event.MouseEvent
import java.util.EventObject

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
        queueTree.addMouseListener(QueueTreeMouseListener(project))
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

class QueueTreeCellRenderer(private val treeModel: DefaultTreeModel) : DefaultTreeCellRenderer() {
    override fun getTreeCellRendererComponent(
        tree: javax.swing.JTree?,
        value: Any?,
        sel: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        val node = value as DefaultMutableTreeNode
        val userObject = node.userObject
        return if (userObject is QueueItem) {
            val item = userObject
            val project = tree?.getClientProperty("project") as IProject
            val panel = JPanel(GridBagLayout())
            val gbc = GridBagConstraints().apply {
                insets = Insets(0, 2, 0, 2)
                anchor = GridBagConstraints.WEST
            }

            // File Path (fixed width, no leading space)
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0
            panel.add(JLabel(item.getDisplayPath(project)).apply {
                preferredSize = Dimension(150, 20)
                maximumSize = Dimension(150, 20) // Prevent stretching
            }, gbc)

            // Prompt
            gbc.gridx = 1; gbc.weightx = 0.0
            panel.add(JLabel(item.prompt).apply {
                preferredSize = Dimension(150, 20)
                maximumSize = Dimension(150, 20)
                border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
                toolTipText = "Click to edit prompt"
            }, gbc)

            // Output Destination
            gbc.gridx = 2; gbc.weightx = 0.0
            panel.add(JLabel(item.outputDestination).apply {
                preferredSize = Dimension(100, 20)
                maximumSize = Dimension(100, 20)
                border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
                toolTipText = "Click to edit output destination"
            }, gbc)

            // Status
            gbc.gridx = 3; gbc.weightx = 0.0
            panel.add(JLabel(item.status.toString()).apply {
                preferredSize = Dimension(80, 20)
            }, gbc)

            // Time
            gbc.gridx = 4; gbc.weightx = 0.0
            panel.add(JLabel(item.getElapsedTime()).apply {
                preferredSize = Dimension(50, 20)
            }, gbc)

            // Save Icon
            if (item.result != null && item.outputDestination.isNotBlank()) {
                val saveIcon = UIManager.getIcon("FileView.floppyDriveIcon") ?: IconLoader.getIcon("/icons/save.png", javaClass)
                val saveButton = JLabel(saveIcon).apply {
                    toolTipText = "Save Result"
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    addMouseListener(object : java.awt.event.MouseAdapter() {
                        override fun mouseClicked(e: java.awt.event.MouseEvent) {
                            AiProcessorToolWindow.saveResult(item, project)
                        }
                    })
                }
                gbc.gridx = 5; gbc.weightx = 0.0
                panel.add(saveButton, gbc)
            }

            // Glue to absorb extra space on the right
            gbc.gridx = 6; gbc.weightx = 1.0
            gbc.fill = GridBagConstraints.HORIZONTAL
            panel.add(Box.createHorizontalGlue(), gbc)

            panel.isOpaque = true
            panel.background = if (sel) getBackgroundSelectionColor() else getBackgroundNonSelectionColor()
            panel.foreground = if (sel) getTextSelectionColor() else getTextNonSelectionColor()
            panel
        } else {
            super.getTreeCellRendererComponent(tree, userObject, sel, expanded, leaf, row, hasFocus)
        }
    }
}

class QueueTreeCellEditor(private val treeModel: DefaultTreeModel) : AbstractCellEditor(), TreeCellEditor {
    private val panel = JPanel(GridBagLayout())
    private lateinit var item: QueueItem
    private lateinit var project: IProject
    private lateinit var promptField: JTextField
    private lateinit var outputField: JTextField

    override fun getTreeCellEditorComponent(
        tree: JTree?,
        value: Any?,
        isSelected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int
    ): Component {
        val node = value as DefaultMutableTreeNode
        val userObject = node.userObject
        if (userObject !is QueueItem) return JLabel(userObject.toString())

        item = userObject
        project = tree?.getClientProperty("project") as IProject
        panel.removeAll()

        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(0, 2, 0, 2)
        }

        // File Path (non-editable)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3
        panel.add(JLabel(item.getDisplayPath(project)).apply { preferredSize = Dimension(150, 20) }, gbc)

        // Prompt (editable)
        gbc.gridx = 1; gbc.weightx = 0.4
        promptField = JTextField(item.prompt, 15).apply {
            addActionListener { stopCellEditing() }
            preferredSize = Dimension(150, 20)
        }
        panel.add(promptField, gbc)

        // Output Destination (editable)
        gbc.gridx = 2; gbc.weightx = 0.3
        outputField = JTextField(item.outputDestination, 10).apply {
            addActionListener { stopCellEditing() }
            preferredSize = Dimension(100, 20)
        }
        panel.add(outputField, gbc)

        // Status (non-editable)
        gbc.gridx = 3; gbc.weightx = 0.0
        panel.add(JLabel(item.status.toString()).apply { preferredSize = Dimension(80, 20) }, gbc)

        // Time (non-editable)
        gbc.gridx = 4; gbc.weightx = 0.0
        panel.add(JLabel(item.getElapsedTime()).apply { preferredSize = Dimension(50, 20) }, gbc)

        // Save Icon
        if (item.result != null && item.outputDestination.isNotBlank()) {
            val saveIcon = UIManager.getIcon("FileView.floppyDriveIcon") ?: IconLoader.getIcon("/icons/save.png", javaClass)
            val saveButton = JLabel(saveIcon).apply {
                toolTipText = "Save Result"
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(object : java.awt.event.MouseAdapter() {
                    override fun mouseClicked(e: java.awt.event.MouseEvent) {
                        AiProcessorToolWindow.saveResult(item, project)
                    }
                })
            }
            gbc.gridx = 5; gbc.weightx = 0.0
            panel.add(saveButton, gbc)
        }

        panel.isOpaque = true
        panel.background = if (isSelected) UIManager.getColor("Tree.selectionBackground") else UIManager.getColor("Tree.background")
        panel.foreground = if (isSelected) UIManager.getColor("Tree.selectionForeground") else UIManager.getColor("Tree.foreground")
        return panel
    }

    override fun getCellEditorValue(): Any {
        item.prompt = promptField.text
        item.outputDestination = outputField.text
        treeModel.nodeChanged(treeModel.root as TreeNode)
        return item
    }

    override fun isCellEditable(event: EventObject?): Boolean {
        if (event !is MouseEvent) return false
        val tree = event.source as? JTree ?: return false
        val path = tree.getPathForLocation(event.x, event.y) ?: return false
        val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return false
        if (node.userObject !is QueueItem) return false

        val bounds = tree.getPathBounds(path) ?: return false
        val x = event.x - bounds.x
        val promptX = 154 // Start after file path (150 + 2 inset + 2 buffer)
        val promptEndX = promptX + 150 // Prompt width
        val outputX = promptEndX + 4 // After prompt (2 insets on each side)
        val outputEndX = outputX + 100 // Output width
        return x in promptX..promptEndX || x in outputX..outputEndX
    }
}