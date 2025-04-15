package com.niloda.aicontext.core.api.domain.acceptance

import com.niloda.aicontext.core.writeMeAnApi

data class TestCase(
    val id: String,
    val description: String,
    val preconditions: String,
    val result: String
)

fun main() {
    val result =  Deliver {
        Deliver.Result.Approve(
            listOf(TestCase("id", "desc", "prcon", "result"))
        )
    }
    println(result.invoke(writeMeAnApi))
}