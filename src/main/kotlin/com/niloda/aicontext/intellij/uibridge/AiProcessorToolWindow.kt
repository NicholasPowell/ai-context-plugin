package com.niloda.aicontext.intellij.uibridge

import com.intellij.openapi.project.Project
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.ui.AiProcessorComposeUI
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