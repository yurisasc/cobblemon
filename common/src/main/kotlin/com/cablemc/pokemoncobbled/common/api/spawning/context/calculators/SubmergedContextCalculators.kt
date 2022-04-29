package com.cablemc.pokemoncobbled.common.api.spawning.context.calculators

import com.cablemc.pokemoncobbled.common.PokemonCobbled.config
import com.cablemc.pokemoncobbled.common.api.spawning.context.SubmergedSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.UnderlavaSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.UnderwaterSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator.Companion.isLavaCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator.Companion.isWaterCondition
import net.minecraft.util.Util.Mth.ceil
import net.minecraft.world.level.block.state.BlockState

/**
 * A spawning context calculator that creates some kind of [SubmergedSpawningContext]. The shared logic
 * of implementations is that there is a fluid condition that must be met at the spawn position.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
interface SubmergedContextCalculator<T : SubmergedSpawningContext> : AreaSpawningContextCalculator<T> {
    val fluidCondition: (BlockState) -> Boolean

    override fun fits(input: AreaSpawningInput) = fluidCondition(input.slice.getBlockState(input.position)) && fluidCondition(input.slice.getBlockState(input.position.below()))
}

/**
 * The context calculator used for [UnderwaterSpawningContext]s. Requires water blocks as the fluid.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
object UnderwaterSpawningContextCalculator : SubmergedContextCalculator<UnderwaterSpawningContext> {
    override val fluidCondition = isWaterCondition

    override fun calculate(input: AreaSpawningInput): UnderwaterSpawningContext {
        return UnderwaterSpawningContext(
            cause = input.cause,
            level = input.level,
            position = input.position.immutable(),
            light = getLight(input),
            skyAbove = getSkyAbove(input),
            influences = input.spawner.copyInfluences(),
            width = getHorizontalSpace(input, fluidCondition, config.maxHorizontalSpace),
            height = getHeight(input, fluidCondition, ceil(config.maxVerticalSpace / 2F)),
            depth = getDepth(input, fluidCondition, ceil(config.maxVerticalSpace / 2F)),
            slice = input.slice,
            nearbyBlocks = getNearbyBlocks(input)
        )
    }
}

/**
 * The context calculator used for [UnderlavaSpawningContext]s. Requires lava blocks as the fluid.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
object UnderlavaSpawningContextCalculator : SubmergedContextCalculator<UnderlavaSpawningContext> {
    override val fluidCondition = isLavaCondition

    override fun calculate(input: AreaSpawningInput): UnderlavaSpawningContext {
        return UnderlavaSpawningContext(
            cause = input.cause,
            level = input.level,
            position = input.position.immutable(),
            light = getLight(input),
            skyAbove = getSkyAbove(input),
            influences = input.spawner.copyInfluences(),
            width = getHorizontalSpace(input, fluidCondition, config.maxHorizontalSpace),
            height = getHeight(input, fluidCondition, ceil(config.maxVerticalSpace / 2F)),
            depth = getDepth(input, fluidCondition, ceil(config.maxVerticalSpace / 2F)),
            slice = input.slice,
            nearbyBlocks = getNearbyBlocks(input)
        )
    }
}

