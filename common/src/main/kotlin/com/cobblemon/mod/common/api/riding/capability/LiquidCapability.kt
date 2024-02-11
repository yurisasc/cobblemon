package com.cobblemon.mod.common.api.riding.capability

import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.util.Identifier
import java.util.function.Predicate

class LiquidCapability(override val properties: RideControllerProperties) : RidingCapability {
    override val key: Identifier = RidingCapability.LIQUID
    override val condition: Predicate<PokemonEntity> = Predicate<PokemonEntity> {
        // TODO - Does the job for now, needs to be adapted to fluid tags to support both water and lava, and customs
        it.isTouchingWater || it.isSubmergedInWater
    }
}