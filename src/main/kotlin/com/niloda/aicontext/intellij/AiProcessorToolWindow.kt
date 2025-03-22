package com.niloda.aicontext.intellij

import androidx.compose.ui.awt.ComposePanel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.niloda.aicontext.intellij.adapt.IntelliJProjectAdapter
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import java.io.File
import javax.swing.JComponent

object AiProcessorToolWindow {
    private lateinit var project: Project

    @OptIn(ExperimentalJewelApi::class)
    fun createComposePanel(proj: Project): JComponent {
        project = proj
        enableNewSwingCompositing()
        return JewelComposePanel({}) {
            SwingBridgeTheme {
                AiProcessorComposeUI(project.adapt())
            }
        }
    }

    fun setResult(item: QueueItem, result: String?) {
        item.result = result ?: "Error: Failed to process file"
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

}