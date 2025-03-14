package com.niloda.aicontext

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GetContextAction : AnAction() {
    private val client = OkHttpClient.Builder()
        .connectTimeout(1800, TimeUnit.SECONDS)
        .readTimeout(1800, TimeUnit.SECONDS)
        .build()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val context = getContext(project, editor, psiFile)

        if (context.isBlank()) {
            Messages.showMessageDialog(project, "No context available to send to Ollama!", "AI Context", Messages.getErrorIcon())
            return
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Fetching AI Context") {
            override fun run(indicator: ProgressIndicator) {
                val statusBar = WindowManager.getInstance().getStatusBar(project)
                statusBar?.setInfo("Processing AI request...")

                AiContextToolWindow.appendOutput("Sending request with ${context.length} chars...\n")
                val prompt = "Explain this code:\n" + context // Default prompt
                val response = sendToOllama(prompt, project)

                com.intellij.util.ui.UIUtil.invokeLaterIfNeeded {
                    if (response != null) {
                        AiContextToolWindow.clearOutput()
                        AiContextToolWindow.appendOutput("Prompt:\n$prompt\n\nResponse:\n$response")
                    } else {
                        AiContextToolWindow.appendOutput("Error: Failed to get response from Ollama")
                    }
                    statusBar?.setInfo("AI request completed")
                }
            }
        })
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = project != null && (editor != null || FileEditorManager.getInstance(project).openFiles.isNotEmpty())
    }

    private fun getContext(project: Project, editor: Editor?, psiFile: PsiFile?): String {
        return when {
            editor != null && editor.selectionModel.hasSelection() -> {
                editor.selectionModel.selectedText ?: ""
            }
            psiFile != null -> {
                psiFile.text ?: ""
            }
            else -> {
                val fileEditorManager = FileEditorManager.getInstance(project)
                val openFiles = fileEditorManager.openFiles
                val textContents = openFiles.mapNotNull { file ->
                    fileEditorManager.getEditors(file).mapNotNull { editor ->
                        (editor as? Editor)?.document?.text
                    }.firstOrNull()
                }
                textContents.joinToString("\n\n---\n\n")
            }
        }
    }

    private fun sendToOllama(prompt: String, project: Project): String? {
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