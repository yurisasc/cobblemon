package com.cablemc.pokemoncobbled.common.entity.pokemon

import com.cablemc.pokemoncobbled.common.api.entity.Despawner
import net.minecraft.entity.Entity

/**
 * The aging despawner applies strictly to mobs that can age. Its logic is relatively simple: the closer to
 * the [nearDistance] that the entity is to a player, the older the entity must be to be despawned. At
 * [nearDistance], an entity must be [maxAgeTicks] to despawn. At [farDistance], an entity must be [minAgeTicks]
 * to despawn. The required age moves gradually for all distances between near and far.
 *
 * @param nearDistance The minimum distance from a player for which an entity can be despawned. If a player is closer
 * than this, then the entity will never be despawned.
 * @param farDistance The minimum distance from a player for which an entity will be kept alive. If no player is within
 * this distance, then the entity will be immediately despawned unless they are younger than [minAgeTicks].
 * @param minAgeTicks The minimum age for an entity before it will be considered for despawning.
 * @param maxAgeTicks The maximum age an entity may be before it will be automatically despawned if it is at least [nearDistance]
 * from a player.
 *
 * @author Hiroku
 * @since March 19th, 2022
 */
class CobbledAgingDespawner<T : Entity>(
    val nearDistance: Float = 12F,
    val farDistance: Float = 42F,
    val minAgeTicks: Int = 20 * 3,
    val maxAgeTicks: Int = 20 * 120,
    val getAgeTicks: (T) -> Int
) : Despawner<T> {

    val nearToFar = farDistance - nearDistance
    val youngToOld = maxAgeTicks - minAgeTicks

    override fun beginTracking(entity: T) {}
    override fun shouldDespawn(entity: T): Boolean {
        val age = getAgeTicks(entity)
        if (age < minAgeTicks || (entity is PokemonEntity && entity.isBusy)) {
            return false
        }

        // TODO an AFK check at some point, don't count the AFK ones.
        val closestDistance = entity.world.players.minOfOrNull { it.distanceTo(entity) } ?: Float.MAX_VALUE
        return when {
            closestDistance < nearDistance -> false
            age > maxAgeTicks || closestDistance > farDistance -> true
            else -> {
                val distanceRatio = (closestDistance - nearDistance) / nearToFar
                val maximumAge = (1 - distanceRatio) * youngToOld
                age > maximumAge
            }
        }
    }
}