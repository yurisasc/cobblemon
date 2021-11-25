package com.cablemc.pokemoncobbled.common.api.reactive

interface CancelableSubscription<T> {
    fun handle(t: T)
    fun cancel()
}