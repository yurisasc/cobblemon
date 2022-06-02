package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.WorldSlice
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A [SpawningContext] that is for a particular area, and therefore has spatial properties.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
open class AreaSpawningContext(
    override val cause: Any,
    override val world: World,
    override val position: BlockPos,
    override val light: Int,
    override val skyAbove: Boolean,
    override val influences: MutableList<SpawningInfluence>,
    /** Space horizontally (diameter) */
    val width: Int,
    /** Space above. */
    val height: Int,
    val nearbyBlocks: Set<BlockState>,
    val slice: WorldSlice
) : SpawningContext() {
    val nearbyBlockTypes = nearbyBlocks.map { it.block.translationKey }
}