package com.niloda.aicontext.model

interface SendToAi {
    operator fun invoke(item: QueueItem, project: IProject)
}