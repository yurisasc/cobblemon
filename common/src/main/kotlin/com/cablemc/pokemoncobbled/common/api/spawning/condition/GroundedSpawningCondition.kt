package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.GroundedSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.Merger
import com.cablemc.pokemoncobbled.common.util.asResource
import net.minecraft.util.Identifier

/**
 * Base type for a spawning condition that applies to some kind of [GroundedSpawningContext]. This
 * can be extended for subclasses of [GroundedSpawningContext].
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
abstract class GroundedTypeSpawningCondition<T : GroundedSpawningContext> : AreaTypeSpawningCondition<T>() {
    var neededBaseBlocks: MutableList<Identifier>? = null

    override fun fits(ctx: T, detail: SpawnDetail): Boolean {
        return if (!super.fits(ctx, detail)) {
            false
        } else !(neededBaseBlocks != null && ctx.baseBlock.block.translationKey.asResource() !in neededBaseBlocks!!)
    }

    override fun copyFrom(other: SpawningCondition<*>, merger: Merger) {
        super.copyFrom(other, merger)
        if (other is GroundedTypeSpawningCondition) {
            neededBaseBlocks = merger.merge(neededBaseBlocks, other.neededBaseBlocks)?.toMutableList()
        }
    }
}

/**
 * A spawning condition for an [GroundedSpawningContext].
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class GroundedSpawningCondition : GroundedTypeSpawningCondition<GroundedSpawningContext>() {
    override fun contextClass() = GroundedSpawningContext::class.java
    companion object {
        const val NAME = "grounded"
    }
}