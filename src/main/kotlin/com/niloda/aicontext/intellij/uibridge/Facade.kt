package com.niloda.aicontext.intellij.uibridge

import com.intellij.openapi.project.Project
import com.niloda.aicontext.intellij.adapt.adapt
import com.niloda.aicontext.intellij.ui.AiProcessorComposeUI
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IntelliJAiFileProcessor
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.SendToAi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import javax.swing.JComponent
import kotlin.collections.minus

object Facade {
    val dataStore = DataStore()
    class DataStore {
        private val _queueFlow: MutableStateFlow<List<QueueItem>> =
            MutableStateFlow<List<QueueItem>>(listOf())
        val queueFlow: StateFlow<List<QueueItem>> = _queueFlow
        fun find(file: IFile): QueueItem? = _queueFlow.value.find { it.file == file }
        fun remove(existingItem: QueueItem) {
            _queueFlow.value -= existingItem
        }
        fun add(item: QueueItem) {
            _queueFlow.value += item
        }
        val size get () = _queueFlow.value.size
        val queueStatus get() = _queueFlow.value.toList()
    }



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