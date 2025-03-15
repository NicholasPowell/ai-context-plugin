package com.niloda.aicontext.impl

import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.UIUtil
import com.niloda.aicontext.model.IProject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiSender {
    private val client = OkHttpClient.Builder()
        .connectTimeout(1800, TimeUnit.SECONDS)
        .readTimeout(1800, TimeUnit.SECONDS)
        .build()

    fun sendToAi(prompt: String, project: IProject): String? {
        return try {
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
                JSONObject(body).getString("response")
            }
        } catch (e: Exception) {
            UIUtil.invokeLaterIfNeeded {
                Messages.showErrorDialog(
                    (project as AiContextServiceImpl.IntelliJProjectAdapter).project,
                    "Failed to contact Ollama: ${e.message}",
                    "AI Context Error"
                )
            }
            null
        }
    }
}