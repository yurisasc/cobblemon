package com.cablemc.pokemoncobbled.forge.client.util

import com.cablemc.pokemoncobbled.forge.common.util.runOnSide
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.LogicalSide

fun <T> runOnClient(block: () -> T) = runOnSide(side = LogicalSide.CLIENT, block)

/**
 * Checks if a resource exists at this location
 */
fun ResourceLocation.exists(): Boolean {
    return Minecraft.getInstance().resourceManager.hasResource(this)
}