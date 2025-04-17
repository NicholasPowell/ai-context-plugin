package com.niloda.aicontext.core.api.domain

data class CashAmount(val dollars: Int, val cents: Int) {
    init {
        assert(dollars >= 0) {
            "Dollar amount must be greater than or equal to zero"
        }
        assert(cents >= 0) {
            "Cents must be greater than or equal to zero"
        }
    }
    companion object {
        val Int.dollars: CashAmount get() = CashAmount(this, 0)
        fun CashAmount.cents(cents: Int) = copy(cents = cents)
    }
    val x = 12.dollars.cents(10)
}