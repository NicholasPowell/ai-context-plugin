package com.niloda.aicontext.model

data class QueueItem(
    val file: IFile,
    var status: Status = Status.PENDING,
    var startTime: Long? = null,
    var prompt: String = "",
    var result: String? = null,
    var outputDestination: String = "",
    var groupName: String = "Default"
) {
    enum class Status { PENDING, RUNNING, DONE, ERROR, CANCELLED }

    fun getElapsedTime(): String {
        return if (startTime != null && status == Status.RUNNING) {
            val elapsed = (System.currentTimeMillis() - startTime!!) / 1000
            "${elapsed}s"
        } else {
            "-"
        }
    }

    fun getDisplayPath(project: IProject): String {
        val projectPath = project.basePath ?: return file.name
        val filePath = file.virtualFilePath
        return filePath.removePrefix(projectPath).trimStart('/')
    }
}