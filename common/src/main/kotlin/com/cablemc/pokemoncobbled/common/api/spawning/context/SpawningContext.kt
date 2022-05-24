package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.condition.BasicSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import net.minecraft.world.biome.Biome

/**
 * A context upon which spawning is being attempted. This supplies all the information that can be used to asynchronously
 * check for suitability of spawning.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
abstract class SpawningContext {
    companion object {
        val contexts = mutableListOf<RegisteredSpawningContext<*>>()
        fun getByName(name: String) = contexts.find { it.name == name }
        fun getByClass(ctx: SpawningContext) = contexts.find { it.clazz == ctx::class.java }
        fun <T : SpawningContext> register(name: String, clazz: Class<T>, defaultCondition: String = BasicSpawningCondition.NAME) {
            contexts.add(
                RegisteredSpawningContext(
                    name = name,
                    clazz = clazz,
                    defaultCondition = defaultCondition
                )
            )
        }
    }

    /** What caused the spawn context. Almost always will be a player entity. */
    abstract val cause: Any
    /** The [World] the spawning context exists in. */
    abstract val world: World
    /** The location of the spawning attempt. */
    abstract val position: BlockPos
    /** The light level at this location. */
    abstract val light: Int
    /** Whether or not the sky is visible at this location. */
    abstract val skyAbove: Boolean
    /** A list of [SpawningInfluence]s that apply due to this specific context. */
    abstract val influences: MutableList<SpawningInfluence>
    /** The current phase of the moon at this location. */
    val moonPhase: Int by lazy { world.moonPhase }
    /** The biome of this location. */
    val biome: Biome by lazy { world.getBiome(position).value() }

    val biomeName: Identifier
        get() = world.registryManager.get(Registry.BIOME_KEY).getId(biome)!!

    /**
     * Filters a spawning detail by some extra condition defined by the context itself. This is for API purposes.
     * @return true if the [SpawnDetail] is acceptable by the context's own logic.
     */
    open fun preFilter(detail: SpawnDetail): Boolean {
        /** Returns true if none of the influences.affectSpawnable return false */
        return influences.none { !it.affectSpawnable(detail, this) }
    }
    open fun afterSpawn(entity: Entity) {
        influences.forEach { it.affectSpawn(entity) }
    }

    open fun getRarity(detail: SpawnDetail): Float {
        var rarity = detail.rarity
        for (influence in influences) {
            rarity = influence.affectRarity(detail, rarity)
        }
        return rarity
    }
}