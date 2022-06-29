package com.cablemc.pokemoncobbled.common.api.spawning.context.calculators

import com.cablemc.pokemoncobbled.common.api.PrioritizedList
import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnCause
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.world.World

/**
 * Calculates some kind of [SpawningContext] from a particular type of input data. This
 * is necessary when you create a new type of [SpawningContext] with [SpawningContext.register]
 * to know how to actually create these contexts.
 *
 * If you are adding to the world spawner, then you probably actually want to create an
 * [AreaSpawningContextCalculator].
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
interface SpawningContextCalculator<I : SpawningContextInput, O : SpawningContext> {
    companion object {
        var foliageMaterials = mutableListOf(
            Material.LEAVES
        )
        val isAirCondition: (BlockState) -> Boolean = { it.isAir || it.material in foliageMaterials }
        val isSolidCondition: (BlockState) -> Boolean = { it.material.isSolid }
        val isWaterCondition: (BlockState) -> Boolean = { it.material == Material.WATER && it.fluidState.isStill  }
        val isLavaCondition: (BlockState) -> Boolean = { it.material == Material.LAVA && it.fluidState.isStill }

        private val calculators = PrioritizedList<SpawningContextCalculator<*, *>>()
        val prioritizedAreaCalculators: List<AreaSpawningContextCalculator<*>>
            get() = calculators.filterIsInstance<AreaSpawningContextCalculator<*>>()

        fun register(calculator: SpawningContextCalculator<*, *>, priority: Priority = Priority.NORMAL) {
            calculators.add(priority, calculator)
        }

        fun unregister(calculator: SpawningContextCalculator<*, *>) {
            calculators.remove(calculator)
        }
    }

    /** Tries creating a [SpawningContext] from the given input. Returning null should be a last resort. */
    fun calculate(input: I): O?
}

/**
 * Base class for input to a [SpawningContextCalculator].
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
open class SpawningContextInput(
    /** What caused the spawn context, as a [SpawnCause]. */
    val cause: SpawnCause,
    /** The [Level] the spawning context exists in. */
    val world: World
)