package com.niloda.aicontext.intellij.adapt

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

fun psiFileAdapter(project: Project, virtualFile: VirtualFile): PsiFile {
    val project = FileEditorManager.getInstance(project).project
    return PsiManager.getInstance(project).findFile(virtualFile)!!
}