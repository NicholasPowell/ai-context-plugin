package com.niloda.aicontext

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

object AiContextQueueManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(1800, TimeUnit.SECONDS)
        .readTimeout(1800, TimeUnit.SECONDS)
        .build()
    val queue = ConcurrentLinkedQueue<QueueItem>()
    private val activeTasks = mutableMapOf<PsiFile, Pair<Task.Backgroundable, ProgressIndicator>>()

    data class QueueItem(val file: PsiFile, var status: Status = Status.PENDING, var startTime: Long? = null) {
        enum class Status { PENDING, RUNNING, DONE, ERROR, CANCELLED }

        fun getElapsedTime(): String {
            return if (startTime != null && status == Status.RUNNING) {
                val elapsed = (System.currentTimeMillis() - startTime!!) / 1000
                "${elapsed}s"
            } else {
                "-"
            }
        }

        fun getDisplayPath(project: Project): String {
            val projectPath = project.basePath ?: return file.name
            val filePath = file.virtualFile.path
            return filePath.removePrefix(projectPath).trimStart('/')
        }
    }

    fun queueFile(file: PsiFile) {
        val existingItem = queue.find { it.file == file }
        if (existingItem != null) {
            queue.remove(existingItem)
            if (existingItem.status == QueueItem.Status.RUNNING) {
                terminate(file)
            }
        }

        val item = QueueItem(file)
        queue.add(item)
        AiContextToolWindow.addToQueue(item, file.project)
        AiContextToolWindow.updateQueue(file.project)
        processNext(file.project)
    }

    private fun processNext(project: Project) {
        val nextItem = queue.find { it.status == QueueItem.Status.PENDING } ?: return
        nextItem.status = QueueItem.Status.RUNNING
        nextItem.startTime = System.currentTimeMillis()
        AiContextToolWindow.updateQueue(project)

        val task = object : Task.Backgroundable(project, "Processing ${nextItem.file.name}", true) {
            private lateinit var indicatorRef: ProgressIndicator

            override fun run(indicator: ProgressIndicator) {
                indicatorRef = indicator
                if (indicator.isCanceled) return

                val prompt = AiContextState.promptTemplate + (nextItem.file.text ?: "")
                val response = sendToOllama(prompt, project, indicator)
                nextItem.status = if (response != null) QueueItem.Status.DONE else QueueItem.Status.ERROR
                nextItem.startTime = null
                AiContextToolWindow.appendOutput("File: ${nextItem.getDisplayPath(project)}\nPrompt:\n$prompt\n\nResponse:\n${response ?: "Error"}\n\n")
                AiContextToolWindow.updateQueue(project)
                activeTasks.remove(nextItem.file)
                processNext(project)
            }

            override fun onCancel() {
                nextItem.status = QueueItem.Status.CANCELLED
                nextItem.startTime = null
                AiContextToolWindow.updateQueue(project)
                activeTasks.remove(nextItem.file)
                processNext(project)
            }
        }
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, object : ProgressIndicatorBase() {
            override fun start() {
                super.start()
                activeTasks[nextItem.file] = task to this
            }
        })
    }

    fun terminate(file: PsiFile) {
        activeTasks[file]?.second?.cancel()
    }

    private fun sendToOllama(prompt: String, project: Project, indicator: ProgressIndicator): String? {
        try {
            val json = JSONObject()
                .put("model", "llama3")
                .put("prompt", prompt)
                .put("stream", false)

            val request = Request.Builder()
                .url("http://localhost:11434/api/generate")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (indicator.isCanceled) return "Cancelled"
                if (!response.isSuccessful) return "Error: ${response.code}"
                val body = response.body?.string() ?: return "No response from Ollama"
                return JSONObject(body).getString("response")
            }
        } catch (e: Exception) {
            com.intellij.util.ui.UIUtil.invokeLaterIfNeeded {
                Messages.showErrorDialog(project, "Failed to contact Ollama: ${e.message}", "AI Context Error")
            }
            return null
        }
    }
}