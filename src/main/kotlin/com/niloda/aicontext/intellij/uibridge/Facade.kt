package com.niloda.aicontext.intellij.uibridge

import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.ModalTaskOwner.project
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.ui.AiProcessorComposeUI
import com.niloda.aicontext.model.SendToAi
import com.niloda.aicontext.model.BackgroundSendToOllama
import com.niloda.aicontext.model.EnqueueFile
import com.niloda.aicontext.ollama.SendToOllama
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import javax.swing.JComponent

class Facade(
    val project: Project,
    val sendToAi: SendToAi,
    val dataStore: DataStore,
    val enqueueFile: EnqueueFile,
    val toolWindow: ResultPersister
) {

    companion object {
        val byProject: MutableMap<Project, Facade> = mutableMapOf()

        @OptIn(ExperimentalJewelApi::class)
        fun createPanel(proj: Project): JComponent {
            val facade = Facade(
                project = proj,
                sendToAi = BackgroundSendToOllama(SendToOllama()),
                dataStore =  DataStore(),
                enqueueFile = EnqueueFile(),
                toolWindow = ResultPersister(proj)
            )
            byProject[proj] = facade
            enableNewSwingCompositing()
            return JewelComposePanel({}) {
                SwingBridgeTheme {
                    AiProcessorComposeUI(
                        queueState = facade.dataStore.queueFlow,
                        project = facade.project.adapt(),
                        sendToAi = facade.sendToAi
                    )
                }
            }
        }
    }
}