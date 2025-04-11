@file:OptIn(ExperimentalJewelApi::class)
package com.niloda.aicontext.intellij.uibridge

import com.intellij.openapi.project.Project
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.ui.queue.QueueComposeUI
import com.niloda.aicontext.intellij.ui.theme.DarculaTheme
import com.niloda.aicontext.model.BackgroundSendToOllama
import com.niloda.aicontext.model.EnqueueFile
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.SendToAi
import com.niloda.aicontext.ollama.SendToOllama
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import javax.swing.JComponent

class QueueFacade(
    val project: Project,
    val sendToAi: SendToAi,
    val queueDataStore: QueueDataStore,
    val enqueueFile: EnqueueFile,
    val toolWindow: ResultPersister
) {

    companion object {
        val byProject: MutableMap<Project, QueueFacade> = mutableMapOf()
        val byName: MutableMap<String, QueueFacade> = mutableMapOf()

        val Project.queueFacade: QueueFacade get() = byProject[this]!!
        val IProject.queueFacade: QueueFacade get() = byName[name]!!

        fun createPanel(proj: Project): JComponent {
            val queueDataStore = QueueDataStore()
            val queueFacade = QueueFacade(
                project = proj,
                sendToAi = BackgroundSendToOllama(SendToOllama()),
                queueDataStore = queueDataStore,
                enqueueFile = EnqueueFile(queueDataStore),
                toolWindow = ResultPersister(proj)
            )
            byProject[proj] = queueFacade
            byName[proj.name] = queueFacade
            enableNewSwingCompositing()
            return JewelComposePanel({}) {
                SwingBridgeTheme {
                    QueueComposeUI(
                        queueState = queueFacade.queueDataStore.queueFlow,
                        project = queueFacade.project.adapt(),
                        sendToAi = queueFacade.sendToAi,
                        theme = { DarculaTheme(it) }
                    )
                }
            }
        }
    }
}