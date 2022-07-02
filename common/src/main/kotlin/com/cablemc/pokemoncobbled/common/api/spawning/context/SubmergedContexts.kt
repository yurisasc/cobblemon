package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnCause
import com.cablemc.pokemoncobbled.common.api.spawning.WorldSlice
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A type of area based spawning context with a fluid base block.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
abstract class SubmergedSpawningContext(
    cause: SpawnCause,
    world: World,
    position: BlockPos,
    light: Int,
    canSeeSky: Boolean,
    influences: MutableList<SpawningInfluence>,
    width: Int,
    height: Int,
    val depth: Int,
    nearbyBlocks: Set<BlockState>,
    slice: WorldSlice
) : AreaSpawningContext(cause, world, position, light, canSeeSky, influences, width, height, nearbyBlocks, slice) {
    val fluidBlock = slice.getBlockState(position.x, position.y, position.z).block
    val fluidState = slice.getBlockState(position.x, position.y, position.z).fluidState
}

/**
 * A spawning context that occurs in pools of water at least 2 blocks deep.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class UnderwaterSpawningContext(
    cause: SpawnCause,
    world: World,
    position: BlockPos,
    light: Int,
    canSeeSky: Boolean,
    influences: MutableList<SpawningInfluence>,
    width: Int,
    height: Int,
    depth: Int,
    nearbyBlocks: Set<BlockState>,
    slice: WorldSlice
): SubmergedSpawningContext(cause, world, position, light, canSeeSky, influences, width, height, depth, nearbyBlocks, slice)

/**
 * A spawning context that occurs in pools of lava at least 2 blocks deep.
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class UnderlavaSpawningContext(
    cause: SpawnCause,
    world: World,
    position: BlockPos,
    light: Int,
    canSeeSky: Boolean,
    influences: MutableList<SpawningInfluence>,
    width: Int,
    height: Int,
    depth: Int,
    nearbyBlocks: Set<BlockState>,
    slice: WorldSlice
): SubmergedSpawningContext(cause, world, position, light, canSeeSky, influences, width, height, depth, nearbyBlocks, slice)