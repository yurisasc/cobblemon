package com.cablemc.pokemoncobbled.common.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3

fun cobbledResource(path: String) = ResourceLocation(PokemonCobbled.MODID, path)

/**
 * Checks if a resource exists at this location
 */
inline fun ResourceLocation.exists(): Boolean {
    return Minecraft.getInstance().resourceManager.hasResource(this)
}