package com.niloda.aicontext.core.api.domain

import com.niloda.aicontext.core.api.domain.instructions.Instructions

data class Work(
    val title: String,
    val id: String,
    val instructions: Instructions,
)

val work = Work(
    id = "work1",
    title = "do stuff",
    instructions = Instructions(
        requirements = listOf(),
        dependencies = listOf(),
    )
)