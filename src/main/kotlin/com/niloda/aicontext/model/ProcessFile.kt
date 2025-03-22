package com.niloda.aicontext.model

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.util.ui.UIUtil
import com.niloda.aicontext.intellij.adapt.IntelliJProjectAdapter
import com.niloda.aicontext.intellij.uibridge.AiProcessorToolWindow

object ProcessFile {
    operator fun invoke(
        activeTasks: MutableMap<IFile, Pair<Task.Backgroundable, ProgressIndicator>>,
        item: QueueItem,
        project: IProject
    ) {
        if (item.status != QueueItem.Status.PENDING) return
        item.status = QueueItem.Status.RUNNING
        item.startTime = System.currentTimeMillis()
        println("Processing file: ${item.file.name}")

        val task = object :
            Task.Backgroundable((project as IntelliJProjectAdapter).project, "Processing ${item.file.name}", true) {
            override fun run(indicator: ProgressIndicator) {
                if (indicator.isCanceled) {
                    println("Task for ${item.file.name} detected cancellation before starting")
                    handleCancellation(item, project)
                    return
                }

                val prompt = item.prompt + (item.file.text ?: "")
                val response = if (!indicator.isCanceled) IntelliJAiFileProcessor.sendToAi(prompt, project) else null
                if (indicator.isCanceled) {
                    println("Task for ${item.file.name} cancelled during execution")
                    handleCancellation(item, project)
                    return
                }

                item.status = if (response != null) QueueItem.Status.DONE else QueueItem.Status.ERROR
                item.startTime = null
                activeTasks.remove(item.file)
                println(
                    "Processing complete for ${item.file.name}, status: ${item.status}, response: ${
                        response?.take(
                            50
                        ) ?: "null"
                    }"
                )

                UIUtil.invokeLaterIfNeeded {
                    AiProcessorToolWindow.setResult(item, response)
                }
            }

            private fun handleCancellation(item: QueueItem, project: IProject) {
                // TODO
            }
        }
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, ProgressIndicatorBase().apply {
            activeTasks[item.file] = task to this
            println("Task started for ${item.file.name}")
        })
    }

}