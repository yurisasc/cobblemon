package com.cablemc.pokemoncobbled.common.api.entity

import net.minecraft.world.entity.Entity

/**
 * Represents a logical despawner for some type of entity. It decides whether an entity should be despawned over time.
 *
 * @author Hiroku
 * @since March 19th, 2022
 */
interface Despawner<T : Entity> {
    fun beginTracking(entity: T)
    fun shouldDespawn(entity: T): Boolean
}