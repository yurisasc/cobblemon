package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextInput
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome

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
        fun <I : SpawningContextInput, T : SpawningContext> register(
            name: String,
            clazz: Class<T>,
            calculator: SpawningContextCalculator<I, T>
        ) {
            contexts.add(
                RegisteredSpawningContext(
                    name = name,
                    clazz = clazz,
                    calculator = calculator
                )
            )
        }
    }

    /** What caused the spawn context. Almost always will be a player entity. */
    abstract val cause: Any
    /** The [Level] the spawning context exists in. */
    abstract val level: Level
    /** The location of the spawning attempt. */
    abstract val position: BlockPos
    /** The light level at this location. */
    abstract val light: Float
    /** Whether or not the sky is visible at this location. */
    abstract val skyAbove: Boolean
    /** The current phase of the moon at this location. */
    val moonPhase: Int by lazy { level.moonPhase }
    /** The biome of this location. */
    val biome: Biome by lazy { level.getBiome(position) }

    /**
     * Filters a spawning detail by some extra condition defined by the context itself. This is for API purposes.
     * @return true if the [SpawnDetail] is acceptable by the context's own logic.
     */
    open fun preFilter(detail: SpawnDetail): Boolean = true
}