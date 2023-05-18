package com.cobblemon.mod.common.api.riding.properties

import com.cobblemon.mod.common.api.riding.conditions.RidingCondition
import com.cobblemon.mod.common.api.riding.types.MountType

interface RidingProperties {

    fun supported(): Boolean

    fun seats(): List<Seat>

    fun conditions(): List<RidingCondition>

    fun properties(type: MountType): MountProperties?

}
