package com.niloda.aicontext.intellij

import com.intellij.openapi.util.IconLoader
import com.niloda.aicontext.QueueUIConstants.FILE_PATH_WIDTH
import com.niloda.aicontext.QueueUIConstants.INSET
import com.niloda.aicontext.QueueUIConstants.OUTPUT_DEST_WIDTH
import com.niloda.aicontext.QueueUIConstants.PROMPT_WIDTH
import com.niloda.aicontext.QueueUIConstants.STATUS_WIDTH
import com.niloda.aicontext.QueueUIConstants.TIME_WIDTH
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import java.awt.Color
import java.awt.Component
import java.awt.Cursor
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.UIManager
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

class QueueTreeCellRenderer(private val treeModel: DefaultTreeModel) : DefaultTreeCellRenderer() {
    override fun getTreeCellRendererComponent(
        tree: JTree?,
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
            createRendererPanel(userObject, tree?.getClientProperty("project") as IProject, sel)
        } else {
            super.getTreeCellRendererComponent(tree, userObject, sel, expanded, leaf, row, hasFocus)
        }
    }
    private fun createRendererPanel(item: QueueItem, project: IProject, isSelected: Boolean): JPanel {
        val panel = JPanel(GridBagLayout()).apply {
            minimumSize = Dimension(600, 30)
            preferredSize = Dimension(600, 30)
        }
        val gbc = GridBagConstraints().apply {
            insets = Insets(INSET, INSET, INSET, INSET)
            anchor = GridBagConstraints.WEST
            fill = GridBagConstraints.NONE
        }

        addFilePathLabel(panel, item, project, gbc)
        addPromptLabel(panel, item, gbc)
        addOutputDestinationLabel(panel, item, gbc)
        addStatusLabel(panel, item, gbc)
        addTimeLabel(panel, item, gbc)
        addRunButton(panel, item, gbc)
        if (item.result != null && item.outputDestination.isNotBlank()) {
            addSaveButton(panel, item, project, gbc)
        }

        // Add horizontal glue to absorb extra space
        gbc.gridx = 7
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        panel.add(Box.createHorizontalGlue(), gbc)

        // Apply selection styling
        panel.isOpaque = true
        panel.background = if (isSelected) getBackgroundSelectionColor() else getBackgroundNonSelectionColor()
        panel.foreground = if (isSelected) getTextSelectionColor() else getTextNonSelectionColor()

        return panel
    }

    private fun addFilePathLabel(panel: JPanel, item: QueueItem, project: IProject, gbc: GridBagConstraints) {
        val label = JLabel(item.getDisplayPath(project)).apply {
            preferredSize = Dimension(FILE_PATH_WIDTH, 20)
            maximumSize = Dimension(FILE_PATH_WIDTH, 20)
            toolTipText = item.getDisplayPath(project) // Full path on hover
        }
        gbc.gridx = 0
        gbc.weightx = 0.0
        panel.add(label, gbc)
    }

    private fun addPromptLabel(panel: JPanel, item: QueueItem, gbc: GridBagConstraints) {
        val label = JLabel(item.prompt).apply {
            preferredSize = Dimension(PROMPT_WIDTH, 20)
            maximumSize = Dimension(PROMPT_WIDTH, 20)
            border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
            toolTipText = "Click to edit prompt"
        }
        gbc.gridx = 1
        gbc.weightx = 0.0
        panel.add(label, gbc)
    }

    private fun addOutputDestinationLabel(panel: JPanel, item: QueueItem, gbc: GridBagConstraints) {
        val label = JLabel(item.outputDestination).apply {
            preferredSize = Dimension(OUTPUT_DEST_WIDTH, 20)
            maximumSize = Dimension(OUTPUT_DEST_WIDTH, 20)
            border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
            toolTipText = "Click to edit output destination"
        }
        gbc.gridx = 2
        gbc.weightx = 0.0
        panel.add(label, gbc)
    }

    private fun addStatusLabel(panel: JPanel, item: QueueItem, gbc: GridBagConstraints) {
        val label = JLabel(item.status.toString()).apply {
            preferredSize = Dimension(STATUS_WIDTH, 20)
        }
        gbc.gridx = 3
        gbc.weightx = 0.0
        panel.add(label, gbc)
    }

    private fun addTimeLabel(panel: JPanel, item: QueueItem, gbc: GridBagConstraints) {
        val label = JLabel(item.getElapsedTime()).apply {
            preferredSize = Dimension(TIME_WIDTH, 20)
        }
        gbc.gridx = 4
        gbc.weightx = 0.0
        panel.add(label, gbc)
    }

    private fun addRunButton(panel: JPanel, item: QueueItem, gbc: GridBagConstraints) {
        val runIcon = IconLoader.getIcon("/actions/execute.svg", javaClass)
        val runButton = JLabel(runIcon).apply {
            toolTipText = "Run this item"
            isEnabled = item.status == QueueItem.Status.PENDING
            if (isEnabled) cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }
        gbc.gridx = 5
        gbc.weightx = 0.0
        panel.add(runButton, gbc)
    }

    private fun addSaveButton(panel: JPanel, item: QueueItem, project: IProject, gbc: GridBagConstraints) {
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
        gbc.gridx = 6
        gbc.weightx = 0.0
        panel.add(saveButton, gbc)
    }

}