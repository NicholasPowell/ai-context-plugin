package com.niloda.aicontext

object QueueUIConstants {
    const val FILE_PATH_WIDTH = 150
    const val PROMPT_WIDTH = 150
    const val OUTPUT_DEST_WIDTH = 100
    const val STATUS_WIDTH = 80
    const val TIME_WIDTH = 50
    const val BUTTON_WIDTH = 24
    const val INSET = 2

    fun getPromptStart(): Int = FILE_PATH_WIDTH + INSET
    fun getOutputDestStart(): Int = getPromptStart() + PROMPT_WIDTH + INSET
    fun getRunButtonStart(): Int = getOutputDestStart() + OUTPUT_DEST_WIDTH + STATUS_WIDTH + TIME_WIDTH + 3 * INSET
    fun getSaveIconStart(): Int = getRunButtonStart() + BUTTON_WIDTH + INSET
}