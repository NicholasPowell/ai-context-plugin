package com.niloda.aicontext.intellij.uibridge

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
    val dataStore = DataStore()
    lateinit var toolWindow: ResultPersister
    lateinit var fileProcessor: IntelliJAiFileProcessor
    lateinit var sendToAi: SendToAi
    lateinit var project: Project

    @OptIn(ExperimentalJewelApi::class)
    fun createComposePanel(proj: Project): JComponent {
        project = proj
        toolWindow = ResultPersister(proj)
        fileProcessor = IntelliJAiFileProcessor()
        sendToAi = SendToAi(fileProcessor)

        enableNewSwingCompositing()
        return JewelComposePanel({}) {
            SwingBridgeTheme {
                AiProcessorComposeUI(
                    queueState = dataStore.queueFlow,
                    project = project.adapt(),
                    sendToAi = sendToAi
                )
            }
        }
    }
}