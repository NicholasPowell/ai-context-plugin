package com.niloda.aicontext.core.api.domain

data class Instructions(
    val request: List<String>,
    val dependencies: List<String>,
    val acceptance: List<String>
)