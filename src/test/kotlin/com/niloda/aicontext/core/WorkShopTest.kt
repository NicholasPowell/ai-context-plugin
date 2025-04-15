package com.niloda.aicontext.core

import com.niloda.aicontext.core.api.InMemoryWorkShop
import com.niloda.aicontext.core.api.domain.Instructions
import com.niloda.aicontext.core.api.domain.Work
import com.niloda.aicontext.core.api.domain.Worker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WorkShopTest {

    @Test
    fun go() {
        // Create initial workshop
        val workshop = InMemoryWorkShop(
            open = emptyMap(),
            commissioned = emptyMap(),
            ready = emptyMap(),
            rework = emptyMap(),
            approved = emptyMap(),
            completed = emptyMap(),
            workers = emptyMap()
        )

        // Create workers
        val worker1 = Worker(id = "worker1", name = "Alice")
        val worker2 = Worker(id = "worker2", name = "Bob")

        // Add workers to the workshop
        var updatedWorkshop = workshop.addWorker(worker1).addWorker(worker2)

        // Create work items
        val work1 = Work(
            id = "work1",
            title = "do stuff",
            instructions = Instructions(
                request = listOf(),
                dependencies = listOf(),
                acceptance = listOf()
            )
        )

        val work2 = Work(
            id = "work2",
            title = "do stuff",
            instructions = Instructions(
                request = listOf(),
                dependencies = listOf(),
                acceptance = listOf()
            )
        )

        val work3 = Work(
            id = "work3",
            title = "do stuff",
            instructions = Instructions(
                request = listOf(),
                dependencies = listOf(),
                acceptance = listOf()
            )
        )

        // Add work items to the workshop
        updatedWorkshop = updatedWorkshop.addWork(work1)
            .addWork(work2)
            .addWork(work3)

        // Assign work to workers
        updatedWorkshop = updatedWorkshop.assign(work1, worker1)
            .assign(work2, worker2)
            .assign(work3, worker1)

        // Approve work
        val commission1 = updatedWorkshop.commissioned[work1.id]!!
        val commission2 = updatedWorkshop.commissioned[work2.id]!!
        val commission3 = updatedWorkshop.commissioned[work3.id]!!

        updatedWorkshop = updatedWorkshop.submit(commission1)
            .submit(commission2)
            .submit(commission3)

        updatedWorkshop = updatedWorkshop.approve(commission1)
            .approve(commission2)
            .approve(commission3)

        // Payout commissions
        updatedWorkshop = updatedWorkshop.payout(commission1)
            .payout(commission2)
            .payout(commission3)

        assertEquals(0, updatedWorkshop.open.size)
        assertEquals(0, updatedWorkshop.commissioned.size)
        assertEquals(0, updatedWorkshop.ready.size)
        assertEquals(0, updatedWorkshop.approved.size)
        assertEquals(3, updatedWorkshop.completed.size)
    }
}