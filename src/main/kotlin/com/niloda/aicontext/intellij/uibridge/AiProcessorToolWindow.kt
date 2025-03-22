package com.niloda.aicontext.intellij.uibridge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import com.intellij.openapi.project.Project
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.ui.AiProcessorComposeUI
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import java.io.File
import javax.swing.JComponent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

object AiProcessorToolWindow {
    private lateinit var project: Project

    val _queueFlow = MutableStateFlow<List<QueueItem>>(listOf())
    val queueFlow: StateFlow<List<QueueItem>> = _queueFlow

    @OptIn(ExperimentalJewelApi::class)
    fun createComposePanel(proj: Project): JComponent {

        project = proj
        enableNewSwingCompositing()
        return JewelComposePanel({}) {
            SwingBridgeTheme {
                Column {
                    Text(
                        text = "Hello, World",
                        modifier = Modifier.background(Color.Red)
                    )
                    AiProcessorComposeUI(
                        queueFlow,
                        project.adapt()
                    )
                }
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