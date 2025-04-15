package com.niloda.aicontext.core

import com.niloda.aicontext.core.api.InMemoryWorkShop
import com.niloda.aicontext.core.api.domain.Instructions
import com.niloda.aicontext.core.api.domain.Work
import com.niloda.aicontext.core.api.domain.Worker

val writeMeAnApi =
    Work(
        title = "Hello World Spring Boot",
        id = "1",
        instructions = Instructions(
            request = listOf("Write me a Spring Boot controller in Kotlin that returns hello, world"),
            acceptance = listOf("Compile this controller and run it in a test harness, ensure it outputs hello, world"),
            dependencies = listOf("Spring boot 3.4.4", "Use no other libraries", "File should be less than 100 lines "),
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
