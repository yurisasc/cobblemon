package com.cablemc.pokemoncobbled.common.api.spawning.context.calculators

import com.cablemc.pokemoncobbled.common.api.spawning.WorldSlice
import com.cablemc.pokemoncobbled.common.api.spawning.context.RegionContextResolver
import com.cablemc.pokemoncobbled.common.api.spawning.context.RegionSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import net.minecraft.core.BlockPos

/**
 * A spawning context calculator that deals with [RegionSpawningContext]s. These work off
 * [RegionSpawningInput] instances, and must output some kind of [RegionSpawningContext].
 *
 * This is the interface to use for all of the contexts that will get used by world spawning
 * as that is the primary case that regional spawning is done. These get registered when
 * creating the context type, in [SpawningContext.register]
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
interface RegionSpawningContextCalculator<O : RegionSpawningContext> : SpawningContextCalculator<RegionSpawningInput, O> {
    /**
     * Whether or not this context calculator is likely to provide a value for this location.
     *
     * This should be relatively final. See how the [RegionContextResolver] works to understand why
     * you should be pretty sure before returning true to this function.
     */
    fun fits(input: RegionSpawningInput): Boolean

    override fun calculate(input: RegionSpawningInput): O? {
        // Do some common calculations and then pass those into an interface function declared here which
        // is responsible for the instantiation
        TODO("Not yet implemented")
    }
}

open class RegionSpawningInput(val position: BlockPos, val slice: WorldSlice) : SpawningContextInput(slice.cause, slice.level)