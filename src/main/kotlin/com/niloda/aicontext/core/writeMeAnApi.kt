package com.niloda.aicontext.core

import com.niloda.aicontext.core.api.InMemoryWorkShop
import com.niloda.aicontext.core.api.domain.instructions.Instructions
import com.niloda.aicontext.core.api.domain.Work
import com.niloda.aicontext.core.api.domain.Worker
import com.niloda.aicontext.core.api.domain.instructions.Dependency
import com.niloda.aicontext.core.api.domain.instructions.Requirement

val writeMeAnApi =
    Work(
        title = "Hello World Spring Boot",
        id = "1",
        instructions = Instructions(
            requirements = listOf(
                Requirement("Write me a Spring Boot controller in Kotlin that returns hello, world")
            ),
            dependencies = listOf(
                Dependency("Spring boot 3.4.4"),
                Dependency("Use no other libraries"),
                Dependency("File should be less than 100 lines ")
            ),
        )
    )

val tammie = Worker(id = "porta", name = "Tammie Gould")
val workshop = InMemoryWorkShop().apply {
    addWork(writeMeAnApi)
    addWorker(tammie)
}
fun main() {
    val a = workshop.assign(writeMeAnApi, tammie)

}
