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
open class SubmergedSpawningContext(
    cause: SpawnCause,
    world: World,
    position: BlockPos,
    light: Int,
    skyAbove: Boolean,
    influences: MutableList<SpawningInfluence>,
    width: Int,
    height: Int,
    val depth: Int,
    nearbyBlocks: Set<BlockState>,
    slice: WorldSlice
) : AreaSpawningContext(cause, world, position, light, skyAbove, influences, width, height, nearbyBlocks, slice) {
    val fluidBlock = slice.getBlockState(position.x, position.y, position.z).block
    val fluidState = slice.getBlockState(position.x, position.y, position.z).fluidState
}