package com.niloda.aicontext.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class WorkShopTest {

    @Test
    fun go() {

        val withWorkers = inMemoryWorkshop
            .addWorker(alice)
            .addWorker(bob)
            .addWork(work1)
            .addWork(work2)
            .addWork(work3)
            .assign(work1, alice)
            .assign(work2, bob)
            .assign(work3, alice)
            .submit(work1)
            .submit(work2)
            .submit(work3)
            .approve(work1)
            .approve(work2)
            .approve(work3)
            .payout(work1)
            .payout(work2)
            .payout(work3)

        assertEquals(0, withWorkers.open.size)
        assertEquals(3, withWorkers.commissioned.size)
        assertEquals(0, withWorkers.ready.size)
        assertEquals(0, withWorkers.approved.size)
        assertEquals(3, withWorkers.completed.size)
    }
}