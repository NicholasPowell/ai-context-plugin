package com.niloda.aicontext.model

import java.util.concurrent.ConcurrentLinkedQueue

interface AiContextService {

    val queue: ConcurrentLinkedQueue<QueueItem>

    fun processPromptForFile(project: IProject, file: IFile?): String
    fun sendToAi(prompt: String, project: IProject): String?
    fun queueFile(file: IFile)
    fun processFile(item: QueueItem, project: IProject)
    fun terminate(file: IFile)
    fun getQueueStatus(): List<QueueItem>
}

interface IProject {
    val name: String
    val basePath: String?
    fun getFileEditorManager(): IFileEditorManager
}

interface IFile {
    val name: String
    val text: String?
    val virtualFilePath: String
}

interface IFileEditorManager {
    val openFiles: List<IFile>
    fun getEditors(file: IFile): List<IEditor>
}

interface IEditor {
    val documentText: String?
    val selectedText: String?
}