package com.niloda.aicontext.intellij

import com.intellij.openapi.options.Configurable
import javax.swing.*

class Settings : Configurable {
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

