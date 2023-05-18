package com.cobblemon.mod.common.api.riding.attributes.provided

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

data class WeightAttribute(val weight: Float) : RidingAttribute {
    override fun identifier(): Identifier {
        return cobblemonResource("weight")
    }
}
