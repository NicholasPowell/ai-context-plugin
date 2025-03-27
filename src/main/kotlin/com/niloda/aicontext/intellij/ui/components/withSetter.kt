package com.niloda.aicontext.intellij.ui.components

// Generic way to use a var boolean without mutableState
infix fun <R: (Boolean) -> Unit> Boolean.withSetter(that: R) = this to that
