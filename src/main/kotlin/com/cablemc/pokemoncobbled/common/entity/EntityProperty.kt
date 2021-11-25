package com.cablemc.pokemoncobbled.common.entity

import com.cablemc.pokemoncobbled.common.api.reactive.CancelableSubscription
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.Entity

/**
 * Entity properties are a wrapping around entityData that can handle subscriptions on either Side.
 */
class EntityProperty<T>(
    private val entity: Entity,
    private val accessor: EntityDataAccessor<T>,
    initialValue: T
) {
    var currentValue: T
        private set
    init {
        entity.entityData.define(accessor, initialValue)
        currentValue = initialValue
    }

    private val subscriptions = mutableListOf<CancelableSubscription<T>>()

    fun listen(takeInitial: Boolean = true, handler: (T) -> Unit): CancelableSubscription<T> {
        val subscription = object : CancelableSubscription<T> {
            override fun handle(t: T) = handler(t)
            override fun cancel() {
                subscriptions.remove(this)
            }
        }

        subscriptions.add(subscription)
        if (takeInitial) {
            subscription.handle(currentValue)
        }
        return subscription
    }

    fun listenUntil(
        takeInitial: Boolean = true,
        handler: (T) -> Unit,
        until: (T) -> Boolean
    ): CancelableSubscription<T> {
        val subscription = object : CancelableSubscription<T> {
            override fun handle(t: T) {
                if (until(t)) {
                    cancel()
                } else {
                    handler(t)
                }
            }

            override fun cancel() {
                subscriptions.remove(this)
            }
        }

        subscriptions.add(subscription)
        if (takeInitial) {
            subscription.handle(currentValue)
        }
        return subscription
    }

    fun checkForUpdate() {
        val newValue = entity.entityData.get(accessor)
        if (newValue != currentValue) {
            currentValue = newValue
            subscriptions.forEachIndexed { _, subscription -> subscription.handle(newValue) }
        }
    }

    fun set(newValue: T) {
        entity.entityData.set(accessor, newValue)
    }
}