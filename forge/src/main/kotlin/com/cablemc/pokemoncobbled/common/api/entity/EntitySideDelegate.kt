package com.cablemc.pokemoncobbled.common.api.entity

import net.minecraft.world.entity.Entity

/**
 * Represents a delegation of a portion of an entity's logic to a particular side.
 */
interface EntitySideDelegate<T : Entity> {
    fun initialize(entity: T) {}
    fun tick(entity: T) {}
}