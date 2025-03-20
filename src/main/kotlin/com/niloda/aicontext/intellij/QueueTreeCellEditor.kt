package com.niloda.aicontext.intellij

import com.intellij.openapi.util.IconLoader
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import java.awt.Component
import java.awt.Cursor
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.EventObject
import javax.swing.AbstractCellEditor
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.JTree
import javax.swing.UIManager
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeCellEditor
import javax.swing.tree.TreeNode

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

        // Run Button
        val runIcon = IconLoader.getIcon("/actions/run.png", javaClass)
        val runButton = JButton(runIcon).apply {
            preferredSize = Dimension(20, 20)
            toolTipText = "Run this item"
            isEnabled = item.status == QueueItem.Status.PENDING
            addActionListener {
                QueueManager.processFile(item, project)
                AiProcessorToolWindow.updateQueue(project)
                AiProcessorToolWindow.startTimer(project)
                stopCellEditing() // Exit editing mode after clicking
            }
        }
        gbc.gridx = 5; gbc.weightx = 0.0
        panel.add(runButton, gbc)

        // Save Icon
        if (item.result != null && item.outputDestination.isNotBlank()) {
            val saveIcon = UIManager.getIcon("FileView.floppyDriveIcon") ?: IconLoader.getIcon("/icons/save.png", javaClass)
            val saveButton = JLabel(saveIcon).apply {
                toolTipText = "Save Result"
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        AiProcessorToolWindow.saveResult(item, project)
                    }
                })
            }
            gbc.gridx = 6; gbc.weightx = 0.0
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