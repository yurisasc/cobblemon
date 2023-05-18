package com.cobblemon.mod.common.api.riding.properties

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute
import com.cobblemon.mod.common.api.riding.types.MountType

interface MountProperties {

    fun type(): MountType

    fun attributes(): List<RidingAttribute>

}