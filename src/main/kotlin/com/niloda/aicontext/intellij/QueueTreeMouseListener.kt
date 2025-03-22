package com.niloda.aicontext.intellij

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.treeStructure.Tree
import com.niloda.aicontext.model.QueueItem
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPopupMenu
import javax.swing.tree.DefaultMutableTreeNode

class QueueTreeMouseListener(private val project: Project) : MouseAdapter() {
    override fun mousePressed(e: MouseEvent) {
        if (e.isPopupTrigger) showPopup(e)
    }

    override fun mouseReleased(e: MouseEvent) {
        if (e.isPopupTrigger) showPopup(e)
    }

    override fun mouseClicked(e: MouseEvent) {
        val tree = e.source as? Tree ?: return
        val path = tree.getPathForLocation(e.x, e.y) ?: return
        val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return
        val item = node.userObject as? QueueItem ?: return

        if (e.clickCount == 2 && item.result != null) {
            openResultInEditor(project, item.file.name, item.result!!)
        }
    }

    private fun showPopup(e: MouseEvent) {
        val tree = e.source as? Tree ?: return
        val path = tree.getPathForLocation(e.x, e.y) ?: return
        val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return
        val item = node.userObject as? QueueItem ?: return

        tree.selectionPath = path
        val popup = JPopupMenu()
        when (item.status) {
            QueueItem.Status.PENDING -> {
                popup.add("Run").addActionListener {
                    QueueManager.processFile(item, project.adapt())
                }
            }
            QueueItem.Status.RUNNING -> {
                popup.add("Cancel").addActionListener {
                    QueueManager.terminate(item.file)
                }
            }
            QueueItem.Status.DONE -> {
                popup.add("Save").addActionListener {
                    AiProcessorToolWindow.saveResult(item, project.adapt())
                }
            }
            else -> return // No actions for ERROR or CANCELLED
        }
        popup.show(tree, e.x, e.y)
    }

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
}