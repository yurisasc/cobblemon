package com.cobblemon.mod.common.api.riding.properties.mounting

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute

interface MountProperties {

    fun type(): MountType

    fun attributes(): List<RidingAttribute>

}