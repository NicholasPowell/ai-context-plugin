package com.niloda.aicontext.core

import com.niloda.aicontext.core.api.domain.instructions.Instructions
import com.niloda.aicontext.core.api.domain.Work

// Create work items
val work1 = Work(
    id = "work1",
    title = "do stuff",
    instructions = Instructions(
        requirements = listOf(),
        dependencies = listOf()
    )
)

val work2 = Work(
    id = "work2",
    title = "do stuff",
    instructions = Instructions(
        requirements = listOf(),
        dependencies = listOf()
    )
)

val work3 = Work(
    id = "work3",
    title = "do stuff",
    instructions = Instructions(
        requirements = listOf(),
        dependencies = listOf()
    )
)