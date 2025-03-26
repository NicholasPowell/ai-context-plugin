package com.niloda.aicontext.model

import com.niloda.aicontext.ollama.SendToOllama

class SendToAi(private val sendToOllama: SendToOllama = SendToOllama()) {

    operator fun invoke(item: QueueItem, project: IProject): String? =
        sendToOllama(
            item.prompt + (item.file.text ?: ""),
            project
        )

}