package com.cablemc.pokemoncobbled.common.api.reactive

/**
 * A straightforward implementation of [Observable] that can emit values but holds no state.
 * This is similar in function to a [java.util.concurrent.CompletableFuture]
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
open class SimpleObservable<T> : Observable<T> {
    private val subscriptions = mutableListOf<ObservableSubscription<T>>()

    override fun subscribe(handler: (T) -> Unit): ObservableSubscription<T> {
        val subscription = ObservableSubscription(this, handler)
        subscriptions.add(subscription)
        return subscription
    }

    override fun unsubscribe(subscription: ObservableSubscription<T>) {
        subscriptions.remove(subscription)
    }

    open fun emit(vararg values: T) {
        values.forEach { value ->
            // One or more of these subscriptions might be removed during emission, snapshot handles this.
            val subscriptionsSnapshot = subscriptions.toList()
            subscriptionsSnapshot.forEach { subscription ->
                // A subscription might unsubscribe OTHER subscriptions, obey immediately.
                if (subscription in subscriptionsSnapshot) {
                    subscription.handle(value)
                }
            }
        }
    }
}