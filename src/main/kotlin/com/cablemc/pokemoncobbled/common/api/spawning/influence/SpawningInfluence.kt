package com.cablemc.pokemoncobbled.common.api.spawning.influence

import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import net.minecraft.world.entity.Entity

/**
 * An influence over spawning that can affect various parts of the spawning process.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
interface SpawningInfluence {
    /** Whether this influence has passed and should be removed. */
    fun isExpired(): Boolean = false
    /** Returns true if the given spawn detail is able to spawn under this influence. */
    fun affectSpawnable(detail: SpawnDetail): Boolean = true
    /** Returns the effective rarity of spawn under this influence. This is after typical rarity multipliers. */
    fun affectRarity(detail: SpawnDetail, rarity: Float): Float = rarity
    /** Applies some influence over the entity that's been spawned. */
    fun affectSpawn(entity: Entity) {}
}