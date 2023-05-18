package com.cobblemon.mod.common.api.riding.attributes.provided

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

data class MomentumAttribute(private val speed: Float, private val acceleration: Float) : RidingAttribute {

    override fun identifier(): Identifier {
        return cobblemonResource("momentum")
    }

    fun speed() : Float {
        return this.speed.coerceIn(1F, 5F)
    }

    fun acceleration() : Float {
        return this.acceleration.coerceIn(1F, 5F)
    }

}
