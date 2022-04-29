package com.cablemc.pokemoncobbled.common.api.spawning.condition

import com.cablemc.pokemoncobbled.common.api.spawning.context.SubmergedSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
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
        if (!super.fits(ctx, detail)) {
            return false
        } else if (depth != null && ctx.depth < depth!!) {
            return false
        } else if (fluidIsSource != null && ctx.fluidState.isSource != fluidIsSource!!) {
            return false
        } else if (fluidBlock != null && ctx.fluidBlock.descriptionId.asResource() != fluidBlock!!) {
            return false
        } else {
            return true
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