package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.AreaSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.Merger
import net.minecraft.util.Identifier

/**
 * Base type for a spawning condition that applies to some kind of [AreaSpawningContext]. This
 * can be extended for subclasses of [AreaSpawningContext].
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
abstract class AreaTypeSpawningCondition<T : AreaSpawningContext> : SpawningCondition<T>() {
    var minWidth: Int? = null
    var maxWidth: Int? = null
    var minHeight: Int? = null
    var maxHeight: Int? = null
    var neededNearbyBlocks: MutableList<Identifier>? = null

    override fun fits(ctx: T, detail: SpawnDetail): Boolean {
        if (!super.fits(ctx, detail)) {
            return false
        } else if (minWidth != null && ctx.width < minWidth!!) {
            return false
        } else if (maxWidth != null && ctx.width > maxWidth!!) {
            return false
        } else if (minHeight != null && ctx.height < minHeight!!) {
            return false
        } else if (maxHeight != null && ctx.height > maxHeight!!) {
            return false
        } else if (neededNearbyBlocks != null && neededNearbyBlocks!!.none { it.toString() !in ctx.nearbyBlockTypes }) {
            return false
        } else {
            return true
        }
    }

    override fun copyFrom(other: SpawningCondition<*>, merger: Merger) {
        super.copyFrom(other, merger)
        if (other is AreaTypeSpawningCondition) {
            if (other.minWidth != null) minWidth = other.minWidth
            if (other.maxWidth != null) maxWidth = other.maxWidth
            if (other.minHeight != null) minHeight = other.minHeight
            if (other.maxHeight != null) maxHeight = other.maxHeight
            neededNearbyBlocks = merger.merge(neededNearbyBlocks, other.neededNearbyBlocks)?.toMutableList()
        }
    }
}

/**
 * A spawning condition for an [AreaSpawningContext].
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
class AreaSpawningCondition : AreaTypeSpawningCondition<AreaSpawningContext>() {
    override fun contextClass(): Class<out AreaSpawningContext> = AreaSpawningContext::class.java
    companion object {
        const val NAME = "area"
    }
}