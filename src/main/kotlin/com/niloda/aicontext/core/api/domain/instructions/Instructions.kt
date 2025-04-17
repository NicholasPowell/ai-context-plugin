package com.niloda.aicontext.core.api.domain.instructions

data class Instructions(
    val requirements: List<Requirement>,
    val dependencies: List<Dependency>
)

data class Requirement(val value: String)


data class Dependency(val value: String)

