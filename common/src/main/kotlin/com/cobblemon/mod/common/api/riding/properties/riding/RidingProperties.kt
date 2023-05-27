package com.cobblemon.mod.common.api.riding.properties.riding

import com.cobblemon.mod.common.api.riding.conditions.RidingCondition
import com.cobblemon.mod.common.api.riding.properties.mounting.MountProperties
import com.cobblemon.mod.common.api.riding.properties.mounting.MountType
import com.cobblemon.mod.common.api.riding.seats.properties.SeatProperties

interface RidingProperties {

    fun supported(): Boolean

    fun seats(): List<SeatProperties>

    fun conditions(): List<RidingCondition>

    fun properties(type: MountType): MountProperties?

}
