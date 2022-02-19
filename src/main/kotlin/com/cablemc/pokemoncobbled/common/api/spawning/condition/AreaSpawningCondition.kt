package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.AreaSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import net.minecraft.resources.ResourceLocation

/**
 * Base type for a spawning condition that applies to some kind of [AreaSpawningContext]. This
 * can be extended for subclasses of [AreaSpawningContext].
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
abstract class AreaTypeSpawningCondition<T : AreaSpawningContext> : SpawningCondition<T>() {
    var minimumWidth: Int? = null
    var maximumWidth: Int? = null
    var minimumHeight: Int? = null
    var maximumHeight: Int? = null
    var neededNearbyBlocks: List<ResourceLocation>? = null

    override fun fits(ctx: T, detail: SpawnDetail): Boolean {
        if (!super.fits(ctx, detail)) {
            return false
        } else if (minimumWidth != null && ctx.width < minimumWidth!!) {
            return false
        } else if (maximumWidth != null && ctx.width > maximumWidth!!) {
            return false
        } else if (minimumHeight != null && ctx.height < minimumHeight!!) {
            return false
        } else if (maximumHeight != null && ctx.height > maximumHeight!!) {
            return false
        } else if (neededNearbyBlocks != null && neededNearbyBlocks!!.none { it !in ctx.nearbyBlockTypes }) {
            return false
        } else {
            return true
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