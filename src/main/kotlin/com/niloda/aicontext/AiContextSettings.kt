package com.niloda.aicontext

import com.intellij.openapi.options.Configurable
import javax.swing.*

class AiContextSettings : Configurable {
    private var promptField: JTextField? = null

    override fun createComponent(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        promptField = JTextField("Explain this code:\n", 30)
        panel.add(JLabel("Prompt Template:"))
        panel.add(promptField)
        return panel
    }

    override fun isModified(): Boolean = true // For simplicity, always save

    override fun apply() {
        AiContextState.promptTemplate = promptField?.text ?: "Explain this code:\n"
    }

    override fun reset() {
        promptField?.text = AiContextState.promptTemplate
    }

    override fun getDisplayName(): String = "Ai-Context Settings"
}

object AiContextState {
    var promptTemplate: String = "Explain this code:\n"
}