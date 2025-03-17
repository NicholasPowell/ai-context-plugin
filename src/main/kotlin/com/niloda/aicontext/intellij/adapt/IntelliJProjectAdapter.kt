package com.niloda.aicontext.intellij.adapt

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.niloda.aicontext.model.IFileEditorManager
import com.niloda.aicontext.model.IProject

class IntelliJProjectAdapter(val project: Project) : IProject {
    override val name: String = project.name
    override val basePath: String? = project.basePath
    override fun getFileEditorManager(): IFileEditorManager = IntelliJFileEditorManagerAdapter(
        project = project,
        manager = FileEditorManager.getInstance(project)
    )
}