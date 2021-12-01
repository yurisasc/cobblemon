package com.cablemc.pokemoncobbled.common.api.reactive

/**
 * A simple implementation of [Observable] that can only emit a singular set of values. Attempts at emitting
 * a second time throws an exception. Subscribing to a completed [SingularObservable] immediately processes
 * the values that were emitted and doesn't bother subscribing as new values will not be emitted.
 *
 * This is similar in function to a [java.util.concurrent.CompletableFuture].
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
open class SingularObservable<T> : SimpleObservable<T>() {
    private var completed = false
    private var completedValue = mutableListOf<T>()
    private val subscriptions = mutableListOf<ObservableSubscription<T>>()

    override fun subscribe(handler: (T) -> Unit): ObservableSubscription<T> {
        val subscription = ObservableSubscription(this, handler)
        if (completed) {
            completedValue.forEach { handler(it) }
        } else {
            subscriptions.add(subscription)
        }
        return subscription
    }

    override fun emit(vararg values: T) {
        if (completed) {
            throw IllegalStateException("This observable is already completed!")
        }
        completed = true
        completedValue.addAll(values)
        super.emit(*values)
        subscriptions.clear()
    }
}