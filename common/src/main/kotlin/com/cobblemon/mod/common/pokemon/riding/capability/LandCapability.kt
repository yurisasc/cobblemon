package com.cobblemon.mod.common.pokemon.riding.capability

import com.cobblemon.mod.common.api.riding.attributes.RidingAttribute
import com.cobblemon.mod.common.api.riding.capabilities.RidingCapability
import com.cobblemon.mod.common.api.riding.properties.MountLocation
import net.minecraft.util.Identifier

class LandCapability(override val attributes: Map<Identifier, RidingAttribute>) : RidingCapability {
    override fun supports(location: MountLocation): Boolean {
        TODO("Not yet implemented")
    }

    override fun tick() {
        TODO("Not yet implemented")
    }
}