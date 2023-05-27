package com.cobblemon.mod.common.pokemon.riding

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute
import com.cobblemon.mod.common.api.riding.properties.mounting.CobblemonMountingTypes
import com.cobblemon.mod.common.api.riding.properties.mounting.MountProperties
import com.cobblemon.mod.common.api.riding.properties.mounting.MountType

class CobblemonMountingProperties : MountProperties {
    override fun type(): MountType {
        return CobblemonMountingTypes.LAND
    }

    override fun attributes(): List<RidingAttribute> {
        return listOf()
    }
}