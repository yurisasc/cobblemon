package com.cablemc.pokemoncobbled.common.api.spawning.context.calculators

import com.cablemc.pokemoncobbled.common.PokemonCobbled.config
import com.cablemc.pokemoncobbled.common.api.spawning.context.FlooredSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.GroundedSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.LavafloorSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.SeafloorSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator.Companion.isAirCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator.Companion.isLavaCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator.Companion.isSolidCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator.Companion.isWaterCondition
import net.minecraft.block.BlockState

/**
 * A spawning context calculator that creates some kind of [FlooredSpawningContext]. The shared
 * idea of these contexts is that there is a base block condition for the floor, and then some
 * other condition for its surroundings.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
interface FlooredSpawningContextCalculator<T : FlooredSpawningContext> : AreaSpawningContextCalculator<T> {
    /** The condition that must be met by the base block. */
    val baseCondition: (BlockState) -> Boolean
    /** The condition that must be met by the surrounding blocks. */
    val surroundingCondition: (BlockState) -> Boolean

    override fun fits(input: AreaSpawningInput): Boolean {
        val floorState = input.slice.getBlockState(input.position)
        val aboveState = input.slice.getBlockState(input.position.up())
        return baseCondition(floorState) && surroundingCondition(aboveState)
    }
}

/**
 * The context calculator used for [GroundedSpawningContext]s. Requires a solid block below it and
 * air blocks in its surroundings.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
object GroundedSpawningContextCalculator : FlooredSpawningContextCalculator<GroundedSpawningContext> {
    // TODO expand base condition
    override val baseCondition: (BlockState) -> Boolean = isSolidCondition
    override val surroundingCondition: (BlockState) -> Boolean = isAirCondition

    override fun calculate(input: AreaSpawningInput): GroundedSpawningContext {
        return GroundedSpawningContext(
            cause = input.cause,
            world = input.world,
            position = input.position.toImmutable(),
            light = getLight(input),
            skyAbove = getSkyAbove(input),
            influences = input.spawner.copyInfluences(),
            width = getHorizontalSpace(input, surroundingCondition, config.maxHorizontalSpace, offsetY = 1),
            height = getHeight(input, surroundingCondition, config.maxVerticalSpace, offsetY = 1),
            slice = input.slice,
            nearbyBlocks = getNearbyBlocks(input)
        )
    }
}

/**
 * The context calculator used for [SeafloorSpawningContext]s. Requires a solid block below it and
 * water blocks in its surroundings.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
object SeafloorSpawningContextCalculator : FlooredSpawningContextCalculator<SeafloorSpawningContext> {
    override val baseCondition: (BlockState) -> Boolean = isSolidCondition
    override val surroundingCondition: (BlockState) -> Boolean = isWaterCondition

    override fun calculate(input: AreaSpawningInput): SeafloorSpawningContext {
        return SeafloorSpawningContext(
            cause = input.cause,
            world = input.world,
            position = input.position.toImmutable(),
            light = getLight(input),
            skyAbove = getSkyAbove(input),
            influences = input.spawner.copyInfluences(),
            width = getHorizontalSpace(input, surroundingCondition, config.maxHorizontalSpace, offsetY = 1),
            height = getHeight(input, surroundingCondition, config.maxVerticalSpace, offsetY = 1),
            slice = input.slice,
            nearbyBlocks = getNearbyBlocks(input)
        )
    }
}

/**
 * The context calculator used for [LavafloorSpawningContext]s. Requires a solid block below it and
 * lava blocks in its surroundings.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
object LavafloorSpawningContextCalculator : FlooredSpawningContextCalculator<LavafloorSpawningContext> {
    override val baseCondition: (BlockState) -> Boolean = isSolidCondition
    override val surroundingCondition: (BlockState) -> Boolean = isLavaCondition

    override fun calculate(input: AreaSpawningInput): LavafloorSpawningContext {
        return LavafloorSpawningContext(
            cause = input.cause,
            world = input.world,
            position = input.position.toImmutable(),
            light = getLight(input),
            skyAbove = getSkyAbove(input),
            influences = input.spawner.copyInfluences(),
            width = getHorizontalSpace(input, surroundingCondition, config.maxHorizontalSpace, offsetY = 1),
            height = getHeight(input, surroundingCondition, config.maxVerticalSpace, offsetY = 1),
            slice = input.slice,
            nearbyBlocks = getNearbyBlocks(input)
        )
    }
}