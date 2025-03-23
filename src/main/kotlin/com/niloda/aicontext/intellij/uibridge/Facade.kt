package com.niloda.aicontext.intellij.uibridge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.intellij.openapi.project.Project
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.ui.AiProcessorComposeUI
import com.niloda.aicontext.model.IntelliJAiFileProcessor
import com.niloda.aicontext.model.SendToAi
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import javax.swing.JComponent

object Facade {
    val dataStore: DataStore = DataStore()
    lateinit var toolWindow: AiProcessorToolWindow
    lateinit var fileProcessor: IntelliJAiFileProcessor
    lateinit var sendToAi: SendToAi

    @OptIn(ExperimentalJewelApi::class)
    fun createComposePanel(proj: Project): JComponent {
        toolWindow = AiProcessorToolWindow(project = proj)
        fileProcessor = IntelliJAiFileProcessor(dataStore)
        sendToAi = SendToAi(fileProcessor)

        enableNewSwingCompositing()
        return JewelComposePanel({}) {
            SwingBridgeTheme {
                AiProcessorComposeUI(
                    queueState = dataStore.queueFlow,
                    project = toolWindow.project.adapt(),
                    sendToAi = sendToAi
                )
            }
        }
    }
}