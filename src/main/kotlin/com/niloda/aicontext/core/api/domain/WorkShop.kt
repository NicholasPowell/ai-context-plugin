package com.niloda.aicontext.core.api.domain

interface WorkShop<E> where E: WorkShop<E> {
    fun addWorker(worker: Worker): E
    fun addWork(work: Work): E
    fun assign(work: Work, worker: Worker): E
    fun submit(work: Work): E
    fun approve(commission: Commission): E
    fun payout(commission: Commission): E
}