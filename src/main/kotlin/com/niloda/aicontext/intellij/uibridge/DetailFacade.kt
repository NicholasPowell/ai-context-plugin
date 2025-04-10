@file:OptIn(ExperimentalJewelApi::class)
package com.niloda.aicontext.intellij.uibridge

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.intellij.openapi.project.Project
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.ui.DetailComposeUI
import com.niloda.aicontext.intellij.ui.QueueComposeUI
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

class DetailFacade(
    val project: Project,
    val currentRow: MutableState<String> = mutableStateOf("Empty")
) {

    companion object {
        val byProject: MutableMap<Project, DetailFacade> = mutableMapOf()
        val byName: MutableMap<String, DetailFacade> = mutableMapOf()

        val Project.detailFacade: DetailFacade get() = byProject[this]!!
        val IProject.detailFacade: DetailFacade get() = byName[name]!!

        fun createPanel(proj: Project): JComponent {
            val detailFacade = DetailFacade(project = proj)
            byProject[proj] = detailFacade
            byName[proj.name] = detailFacade
            enableNewSwingCompositing()
            return JewelComposePanel({}) {
                SwingBridgeTheme {
                    DetailComposeUI(detailFacade.currentRow) { DarculaTheme { it() } }
                }
            }
        }
    }
}