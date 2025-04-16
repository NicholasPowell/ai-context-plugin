package com.niloda.aicontext.core

import com.niloda.aicontext.core.api.InMemoryWorkShop

val inMemoryWorkshop = InMemoryWorkShop(
    open = emptyMap(),
    commissioned = emptyMap(),
    ready = emptyMap(),
    rework = emptyMap(),
    approved = emptyMap(),
    completed = emptyMap(),
    workers = emptyMap()
)