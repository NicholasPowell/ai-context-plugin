package com.niloda.aicontext.core.api.domain

import com.niloda.aicontext.core.api.domain.acceptance.TestCase

/**
 * The terms and details of
 * What work is to be done
 * By whom
 * Acceptance conditions
 * And compensation structure
 */
data class Commission(
    val work: Work,
    val worker: Worker,
    val acceptance: List<TestCase>,
    val compensation: String
)

/**
 * Compensation can be fixed, hourly, all-or-nothing, or weighted by acceptance criteria
 */
data class Compensation(
    val type: String,
    val maximum: CashAmount
) {
    enum class Type {
        FIXED,
        HOURLY,
        ALL_OR_NOTHING,
        WEIGHTED_BY_CRITERIA
    }
}

