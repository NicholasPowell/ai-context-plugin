package com.niloda.aicontext.intellij.adapt

import com.intellij.psi.PsiFile
import com.niloda.aicontext.model.IFile

class IntelliJFileAdapter(val psiFile: PsiFile) : IFile {
    override val name: String = psiFile.name
    override val text: String? = psiFile.text
    override val virtualFilePath: String = psiFile.virtualFile.path
}