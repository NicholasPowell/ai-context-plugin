package com.niloda.aicontext.core.api

import com.niloda.aicontext.core.api.domain.Commission
import com.niloda.aicontext.core.api.domain.Work
import com.niloda.aicontext.core.api.domain.WorkShop
import com.niloda.aicontext.core.api.domain.Worker

/**
 * In memory implementation of work shop flow with hash maps and immutable operations
 */
data class InMemoryWorkShop(
    val open: Map<String, Work> = mapOf(),
    val commissioned: Map<String, Commission> = mapOf(),
    val ready: Map<String, Commission> = mapOf(),
    val rework: Map<String, Commission> = mapOf(),
    val approved: Map<String, Commission> = mapOf(),
    val completed: Map<String, Commission> = mapOf(),
    val workers: Map<String, Worker> = mapOf()
): WorkShop {

    override fun addWorker(worker: Worker): InMemoryWorkShop = copy(workers = workers + Pair(worker.id, worker))

    override fun addWork(work: Work): InMemoryWorkShop = copy(open = open + Pair(work.id, work))

    override fun assign(work: Work, worker: Worker): InMemoryWorkShop =
        copy(
            open = open - work.id,
            commissioned = commissioned +
                    Pair(
                        work.id,
                        Commission(
                            work = work,
                            worker = worker,
                            approval = "no",
                            payment = "none"
                        )
                    )
        )

    override fun submit(work: Work): InMemoryWorkShop =
        commissioned[work.id] ?.let {
            copy(
                commissioned = commissioned - work.id,
                ready = ready + Pair(work.id, it)
            )
        } ?: this

    override fun approve(commission: Commission): InMemoryWorkShop =
        copy(
            ready = ready - commission.work.id,
            approved = approved + Pair(commission.work.id, commission)
        )

    override fun payout(commission: Commission): InMemoryWorkShop =
        copy(
            approved = approved - commission.work.id,
            completed = completed + Pair(commission.work.id, commission)
        )
}