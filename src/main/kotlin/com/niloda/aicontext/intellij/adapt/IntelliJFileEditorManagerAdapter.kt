package com.niloda.aicontext.intellij.adapt

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.niloda.aicontext.model.IEditor
import com.niloda.aicontext.model.IFile
import com.niloda.aicontext.model.IFileEditorManager

class IntelliJFileEditorManagerAdapter(
    private val manager: FileEditorManager,
    private val project: Project
) : IFileEditorManager {
    override val openFiles: List<IFile> = manager.openFiles.map {
        IntelliJFileAdapter(
            psiFileAdapter(project, it)
        )
    }
    override fun getEditors(file: IFile): List<IEditor> {
        val psiFile = (file as? IntelliJFileAdapter)?.psiFile ?: return emptyList()
        return manager.getEditors(psiFile.virtualFile).map { IntelliJEditorAdapter(it as Editor) }
    }
}