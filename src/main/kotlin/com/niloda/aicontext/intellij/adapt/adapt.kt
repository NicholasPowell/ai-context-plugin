package com.niloda.aicontext.intellij.adapt

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

fun Project.adapt() = IntelliJProjectAdapter(this)
fun PsiFile.adapt() = IntelliJFileAdapter(this)