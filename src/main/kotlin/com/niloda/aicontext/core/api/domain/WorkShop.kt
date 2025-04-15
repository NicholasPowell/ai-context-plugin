package com.niloda.aicontext.core.api.domain

import com.niloda.aicontext.core.api.InMemoryWorkShop

interface WorkShop {
    fun addWorker(worker: Worker): InMemoryWorkShop
    fun addWork(work: Work): InMemoryWorkShop
    fun assign(work: Work, worker: Worker): InMemoryWorkShop
    fun submit(work: Work): InMemoryWorkShop
    fun approve(commission: Commission): InMemoryWorkShop
    fun payout(commission: Commission): InMemoryWorkShop
}