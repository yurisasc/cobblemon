package com.cobblemon.mod.common.api.riding.attributes

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

data class WeightAttribute(val value: Float) : RidingAttribute {

    override val identifier: Identifier = cobblemonResource("weight")

}