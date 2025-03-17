package com.niloda.aicontext.intellij

import com.intellij.ui.table.JBTable
import javax.swing.JTable

fun JBTable.applyTableDimensions() {
    apply {
        autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
        columnModel.getColumn(0).preferredWidth = 300
        columnModel.getColumn(1).preferredWidth = 400
        columnModel.getColumn(2).preferredWidth = 200 // New column width
        columnModel.getColumn(3).maxWidth = 80
        columnModel.getColumn(4).maxWidth = 100
        columnModel.getColumn(5).maxWidth = 80
        rowHeight = 25 // Default height for file rows
    }
}