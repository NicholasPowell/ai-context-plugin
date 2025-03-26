package com.niloda.aicontext.intellij.uibridge

import com.intellij.openapi.project.Project
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.ui.AiProcessorComposeUI
import com.niloda.aicontext.model.EnqueueFile
import com.niloda.aicontext.model.SendToAi
import com.niloda.aicontext.ollama.SendToOllama
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import javax.swing.JComponent

object Facade {

    val sendToAi: SendToAi by lazy { SendToAi(SendToOllama()) }
    val dataStore: DataStore by lazy { DataStore() }
    val enqueueFile: EnqueueFile by lazy { EnqueueFile() }

    lateinit var project: Project
    lateinit var toolWindow: ResultPersister

    @OptIn(ExperimentalJewelApi::class)
    fun createPanel(proj: Project): JComponent {
        project = proj
        toolWindow = ResultPersister(proj)

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