package com.niloda.aicontext.core.api.domain

data class Commission(
    val work: Work,
    val worker: Worker,
    val approval: String,
    val payment: String
)