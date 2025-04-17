package com.niloda.aicontext.core.api.domain.acceptance

import com.niloda.aicontext.core.api.domain.Worker
import com.niloda.aicontext.core.api.domain.actions.Verify
import com.niloda.aicontext.core.writeMeAnApi
import java.time.Instant



data class TestCase(
    val description: String,
    val preconditions: String,
    val result: String,
    val weight: Int
)

data class TestResult(
    val testCase: TestCase,
    val pass: Boolean,
    val percentPass: Int
)

data class TestRequest(
    val testCase: TestCase,
    val worker: Worker,
    val due: Instant
)


fun main() {
    val result = Verify {
        Verify.Result.Approve(
            listOf(
                TestCase(
                    description = "desc",
                    preconditions = "prcon",
                    result = "result",
                    weight = 1
                )
            )
        )
    }
    println(result.invoke(writeMeAnApi))
}