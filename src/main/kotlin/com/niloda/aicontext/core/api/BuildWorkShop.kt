package com.niloda.aicontext.core.api

import com.niloda.aicontext.core.api.domain.WorkShop

fun interface BuildWorkShop<E> where E:WorkShop<E> {
    operator fun invoke(): WorkShop<E>
}