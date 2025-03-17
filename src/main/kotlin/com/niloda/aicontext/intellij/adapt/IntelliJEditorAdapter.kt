package com.niloda.aicontext.intellij.adapt

import com.intellij.openapi.editor.Editor
import com.niloda.aicontext.model.IEditor

class IntelliJEditorAdapter(editor: Editor) : IEditor {
    override val documentText: String? = editor.document.text
    override val selectedText: String? = editor.selectionModel.selectedText
}