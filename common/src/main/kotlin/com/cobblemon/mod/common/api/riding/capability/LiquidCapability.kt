package com.cobblemon.mod.common.api.riding.capability

import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.blockPositionsAsListRounded
import net.minecraft.util.Identifier
import net.minecraft.util.shape.VoxelShapes
import java.util.function.Predicate

class LiquidCapability(override val properties: RideControllerProperties) : RidingCapability {
    override val key: Identifier = RidingCapability.LIQUID
    //I've tested this with lava, and the pred works properly, but the pokemon sinks in the lava
    override val condition: Predicate<PokemonEntity> = Predicate<PokemonEntity> { entity->
        //This could be kinda weird... what if the top of the mon is in a fluid but the bottom isnt?
        return@Predicate VoxelShapes.cuboid(entity.boundingBox).blockPositionsAsListRounded().any {
            if (entity.isTouchingWater || entity.isSubmergedInWater) {
                return@any true
            }
            val blockState = entity.world.getBlockState(it)
            return@any !blockState.fluidState.isEmpty
        }
    }
}