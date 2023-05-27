package com.cobblemon.mod.common.api.riding.properties.mounting

import net.minecraft.util.Identifier

object CobblemonMountingTypes {

    @JvmField
    val LAND : MountType = create(Identifier("cobblemon", "land"))

    @JvmField
    val WATER : MountType = create(Identifier("cobblemon", "water"))

    @JvmField
    val AIR : MountType = create(Identifier("cobblemon", "air"))

    private fun create(identifier: Identifier) : MountType {
        return MountType(identifier)
    }
}