package com.niloda.aicontext.core.api.domain

data class Work(
    val title: String,
    val id: String,
    val instructions: Instructions,
)

val work = Work(
    id = "work1",
    title = "do stuff",
    instructions = Instructions(
        request = listOf(),
        dependencies = listOf(),
        acceptance = listOf()
    )
)