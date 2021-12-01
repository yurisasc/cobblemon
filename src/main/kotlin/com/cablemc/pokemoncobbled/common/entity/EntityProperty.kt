package com.cablemc.pokemoncobbled.common.entity

import com.cablemc.pokemoncobbled.common.api.reactive.SettableObservable
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData

/**
 * Entity properties are a wrapping around entityData that can handle subscriptions on either Side.
 */
class EntityProperty<T>(
    private val entityData: SynchedEntityData,
    private val accessor: EntityDataAccessor<T>,
    initialValue: T
): SettableObservable<T>(initialValue) {
    init {
        entityData.define(accessor, initialValue)
    }

    fun checkForUpdate() {
        val newValue = entityData.get(accessor)
        if (newValue != get()) {
            super.set(newValue)
        }
    }

    override fun set(newValue: T) {
        entityData.set(accessor, newValue)
        super.set(newValue)
    }
}