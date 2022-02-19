package com.cablemc.pokemoncobbled.common.api.reactive

class ObservableSubscription<T>(
    private val observable: Observable<T>,
    private val handler: (T) -> Unit
) {
    fun handle(value: T) = handler(value)
    fun unsubscribe() = observable.unsubscribe(this)
}