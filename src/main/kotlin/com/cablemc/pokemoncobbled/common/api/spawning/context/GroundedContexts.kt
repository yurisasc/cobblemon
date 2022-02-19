package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.WorldSlice
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

/**
 * A type of area based spawning context with a floor.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
abstract class FlooredSpawningContext(
    cause: Any,
    level: Level,
    position: BlockPos,
    light: Int,
    skyAbove: Boolean,
    influences: MutableList<SpawningInfluence>,
    width: Int,
    height: Int,
    nearbyBlocks: Set<BlockState>,
    slice: WorldSlice
) : AreaSpawningContext(cause, level, position, light, skyAbove, influences, width, height, nearbyBlocks, slice) {
    /** The block that the spawning is occurring on. */
    val baseBlock = slice.getBlockState(position.x, position.y, position.z)
}

/**
 * A land spawning context.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class GroundedSpawningContext(
    cause: Any,
    level: Level,
    position: BlockPos,
    light: Int,
    skyAbove: Boolean,
    influences: MutableList<SpawningInfluence>,
    width: Int,
    height: Int,
    nearbyBlocks: Set<BlockState>,
    slice: WorldSlice
) : FlooredSpawningContext(cause, level, position, light, skyAbove, influences, width, height, nearbyBlocks, slice)

/**
 * A spawning context that occurs at the bottom of a body of water.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class SeafloorSpawningContext(
    cause: Any,
    level: Level,
    position: BlockPos,
    light: Int,
    skyAbove: Boolean,
    influences: MutableList<SpawningInfluence>,
    width: Int,
    height: Int,
    nearbyBlocks: Set<BlockState>,
    slice: WorldSlice
) : FlooredSpawningContext(cause, level, position, light, skyAbove, influences, width, height, nearbyBlocks, slice)

/**
 * A spawning context that occurs at the bottom of bodies of lava.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class LavafloorSpawningContext(
    cause: Any,
    level: Level,
    position: BlockPos,
    light: Int,
    skyAbove: Boolean,
    influences: MutableList<SpawningInfluence>,
    width: Int,
    height: Int,
    nearbyBlocks: Set<BlockState>,
    slice: WorldSlice
) : FlooredSpawningContext(cause, level, position, light, skyAbove, influences, width, height, nearbyBlocks, slice)