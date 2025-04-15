package com.niloda.aicontext.core.api.domain.actions

import com.niloda.aicontext.core.api.domain.Work
import com.niloda.aicontext.core.api.domain.acceptance.TestCase

fun interface Payout {
    operator fun invoke(work: Work): Result
    sealed interface Result {
        val successfulCases: List<TestCase>
        data class Approve(override val successfulCases: List<TestCase>) : Result
        data class Reject(
            override val successfulCases: List<TestCase>,
            val unsuccessfulTestCases: List<TestCase>
        ) : Result
    }
}