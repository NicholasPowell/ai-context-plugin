@file:OptIn(ExperimentalJewelApi::class)
package com.niloda.aicontext.intellij.uibridge

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.niloda.aicontext.intellij.ui.details.DetailComposeUI
import com.niloda.aicontext.intellij.ui.theme.DarculaTheme
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import javax.swing.JComponent


class DetailDataStore(
    val queueFlow: MutableStateFlow<QueueItem?> = MutableStateFlow(null)
)
class DetailFacade(
    val project: Project,
    val dataStore: DetailDataStore
) {

    companion object {
        val byProject: MutableMap<Project, DetailFacade> = mutableMapOf()
        val byName: MutableMap<String, DetailFacade> = mutableMapOf()

        val Project.detailFacade: DetailFacade get() = byProject[this]!!
        val IProject.detailFacade: DetailFacade get() = byName[name]!!

        fun createPanel(proj: Project): JComponent {
            val detailFacade = DetailFacade(project = proj, dataStore = DetailDataStore())
            byProject[proj] = detailFacade
            byName[proj.name] = detailFacade
            enableNewSwingCompositing()

            detailFacade.dataStore.queueFlow.value = QueueItem(
                file = object: IFile {
                    override val name: String = "file"
                    override val text: String? = "text"
                    override val virtualFilePath: String = "./path"
                },
                status = QueueItem.Status.PENDING,
                startTime = 1904,
                prompt = "ponderum",
                result = "sea",
                outputDestination = "utroque",
                groupName = "Frederic Schmidt"
            )

            return JewelComposePanel({}) {
                SwingBridgeTheme {
                    DetailComposeUI(detailFacade.dataStore.queueFlow) { DarculaTheme { it() } }
                }
            }
        }
    }
}