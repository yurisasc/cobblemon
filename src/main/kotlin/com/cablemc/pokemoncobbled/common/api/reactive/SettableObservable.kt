package com.cablemc.pokemoncobbled.common.api.reactive

/**
 * A type of [Observable] that wraps around a value. This means you can immediately get the current value of
 * the observable, and can set a new value yourself.
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
open class SettableObservable<T>(private var value: T) : Observable<T> {
    val subscriptions = mutableListOf<ObservableSubscription<T>>()

    fun subscribeIncludingCurrent(handler: (T) -> Unit): ObservableSubscription<T> {
        val subscription = subscribe(handler)
        subscription.handle(value)
        return subscription
    }

    override fun subscribe(handler: (T) -> Unit): ObservableSubscription<T> {
        val subscription = ObservableSubscription(this, handler)
        subscriptions.add(subscription)
        return subscription
    }

    override fun unsubscribe(subscription: ObservableSubscription<T>) {
        subscriptions.remove(subscription)
    }

    open fun set(newValue: T) {
        if (this.value?.equals(newValue) == true || (this.value == null && newValue == null)) {
            return
        }
        this.value = newValue
        subscriptions.forEach { it.handle(newValue) }
    }

    open fun get() = this.value
}