package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.SubmergedSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.util.Merger
import com.cablemc.pokemoncobbled.common.util.asResource
import net.minecraft.util.Identifier

/**
 * Base type for a spawning condition that applies to some kind of [SubmergedSpawningContext]. This
 * can be extended for subclasses of [SubmergedSpawningContext].
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
abstract class SubmergedTypeSpawningCondition<T : SubmergedSpawningContext> : AreaTypeSpawningCondition<T>() {
    var depth: Int? = null
    var fluidIsSource: Boolean? = null
    var fluidBlock: Identifier? = null

    override fun fits(ctx: T, detail: SpawnDetail): Boolean {
        return if (!super.fits(ctx, detail)) {
            false
        } else if (depth != null && ctx.depth < depth!!) {
            false
        } else if (fluidIsSource != null && ctx.fluidState.isStill != fluidIsSource!!) {
            false
        } else !(fluidBlock != null && ctx.fluidBlock.translationKey.asResource() != fluidBlock!!)
    }

    override fun copyFrom(other: SpawningCondition<*>, merger: Merger) {
        super.copyFrom(other, merger)
        if (other is SubmergedTypeSpawningCondition) {
            if (other.depth != null) depth = other.depth
            if (other.fluidIsSource != null) fluidIsSource = other.fluidIsSource
            if (other.fluidBlock != null) fluidBlock = other.fluidBlock
        }
    }
}

/**
 * A spawning condition for an [SubmergedSpawningContext].
 *
 * @author Hiroku
 * @since February 7th, 2022
 */
open class SubmergedSpawningCondition : SubmergedTypeSpawningCondition<SubmergedSpawningContext>() {
    override fun contextClass() = SubmergedSpawningContext::class.java
    companion object {
        const val NAME = "submerged"
    }
}