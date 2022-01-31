package com.cablemc.pokemoncobbled.common.api.spawning.context.calculators

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import net.minecraft.world.level.Level

/**
 * Calculates some kind of [SpawningContext] from a particular type of input data. This
 * is necessary when you create a new type of [SpawningContext] with [SpawningContext.register]
 * to know how to actually create these contexts.
 *
 * If you are adding to the world spawner, then you probably actually want to create a
 * [RegionSpawningContextCalculator].
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
interface SpawningContextCalculator<I : SpawningContextInput, O : SpawningContext> {
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
    /** What caused the spawn context. Almost always will be a player entity. */
    val cause: Any,
    /** The [Level] the spawning context exists in. */
    val level: Level
)