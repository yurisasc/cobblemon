package com.cablemc.pokemoncobbled.common.entity

import com.cablemc.pokemoncobbled.common.api.reactive.SettableObservable
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData

/**
 * Entity properties are a wrapping around entityData that can handle subscriptions on either Side.
 */
class EntityProperty<T>(
    private val dataTracker: DataTracker,
    private val accessor: TrackedData<T>,
    initialValue: T
): SettableObservable<T>(initialValue) {
    init {
        dataTracker.startTracking(accessor, initialValue)
    }

    fun checkForUpdate() {
        val newValue = dataTracker.get(accessor)
        if (newValue != get()) {
            super.set(newValue)
        }
    }

    override fun set(newValue: T) {
        dataTracker.set(accessor, newValue)
        super.set(newValue)
    }
}