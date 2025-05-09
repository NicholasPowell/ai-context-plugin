package com.niloda.aicontext.ollama

import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.UIUtil
import com.niloda.aicontext.intellij.adapt.IntelliJProjectAdapter
import com.niloda.aicontext.model.IProject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SendToOllama {
    private val client = OkHttpClient.Builder()
        .connectTimeout(1800, TimeUnit.SECONDS)
        .readTimeout(1800, TimeUnit.SECONDS)
        .build()

    operator fun invoke(prompt: String, project: IProject): String? =
        try {

            println("Sending to AI $prompt")
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
                    (project as IntelliJProjectAdapter).project,
                    "Failed to contact Ollama: ${e.message}",
                    "AI Context Error"
                )
            }
            null
        }
}