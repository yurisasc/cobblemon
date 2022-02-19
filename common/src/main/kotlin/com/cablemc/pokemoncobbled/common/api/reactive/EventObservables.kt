package com.cablemc.pokemoncobbled.common.api.reactive

import com.cablemc.pokemoncobbled.common.api.events.Cancelable

open class EventObservable<T> : SimpleObservable<T>() {
    fun post(event: T, then: (T) -> Unit = {}) {
        emit(event)
        then(event)
    }
}

open class CancelableObservable<T : Cancelable> : EventObservable<T>() {
    override fun emit(vararg values: T) {
        if (subscriptions.isEmpty()) {
            return
        }

        values.forEach { value ->
            // One or more of these subscriptions might be removed during emission, snapshot handles this.
            val subscriptionsSnapshot = subscriptions.toList()
            // Stop emitting a value once the value is canceled.
            subscriptionsSnapshot.firstOrNull { subscription ->
                subscription.handle(value)
                return@firstOrNull value.isCanceled
            }
        }
    }

    fun postThen(event: T, ifCanceled: (T) -> Unit = {}, ifSucceeded: (T) -> Unit) {
        post(event) {
            if (it.isCanceled) {
                ifCanceled(it)
            } else {
                ifSucceeded(it)
            }
        }
    }
}