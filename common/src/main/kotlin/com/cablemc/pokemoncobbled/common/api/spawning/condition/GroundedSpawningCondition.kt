package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.GroundedSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.asResource
import net.minecraft.resources.ResourceLocation

/**
 * Base type for a spawning condition that applies to some kind of [GroundedSpawningContext]. This
 * can be extended for subclasses of [GroundedSpawningContext].
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
abstract class GroundedTypeSpawningCondition<T : GroundedSpawningContext> : AreaTypeSpawningCondition<T>() {
    var neededBaseBlocks: List<ResourceLocation>? = null

    override fun fits(ctx: T, detail: SpawnDetail): Boolean {
        if (!super.fits(ctx, detail)) {
            return false
        } else if (neededBaseBlocks != null && ctx.baseBlock.block.descriptionId.asResource() !in neededBaseBlocks!!) {
            return false
        } else {
            return true
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