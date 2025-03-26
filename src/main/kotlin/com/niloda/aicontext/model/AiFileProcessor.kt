package com.niloda.aicontext.model

interface AiFileProcessor {
    fun processFile(item: QueueItem, project: IProject)
    fun terminate(file: IFile)
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