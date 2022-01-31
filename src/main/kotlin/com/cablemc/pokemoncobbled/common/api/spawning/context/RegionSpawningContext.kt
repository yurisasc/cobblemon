package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

/**
 * A [SpawningContext] that is for a particular region, and therefore has
 * spatial properties.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
open class RegionSpawningContext(
    override val cause: Any,
    override val level: Level,
    override val position: BlockPos,
    override val light: Float,
    override val skyAbove: Boolean,
    /** Space horizontal (absolute) */
    val width: Int,
    /** Space above. */
    val height: Int,
    /** Space below. */
    val depth: Int,
    val nearbyBlocks: Iterable<ResourceLocation>,
    val influences: Iterable<SpawningInfluence>
) : SpawningContext()