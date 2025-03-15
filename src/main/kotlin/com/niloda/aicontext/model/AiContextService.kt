package com.niloda.aicontext.model

import java.util.concurrent.ConcurrentLinkedQueue


interface AiContextService {
    data class QueueItem(
        val file: IFile,
        var status: Status = Status.PENDING,
        var startTime: Long? = null,
        var prompt: String = ""
    ) {
        enum class Status { PENDING, RUNNING, DONE, ERROR, CANCELLED }

        fun getElapsedTime(): String {
            return if (startTime != null && status == Status.RUNNING) {
                val elapsed = (System.currentTimeMillis() - startTime!!) / 1000
                "${elapsed}s"
            } else {
                "-"
            }
        }

        fun getDisplayPath(project: IProject): String {
            val projectPath = project.basePath ?: return file.name
            val filePath = file.virtualFilePath
            return filePath.removePrefix(projectPath).trimStart('/')
        }
    }

    val queue: ConcurrentLinkedQueue<QueueItem>

    fun getContext(project: IProject, editorText: String?, file: IFile?): String
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