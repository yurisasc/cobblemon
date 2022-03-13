package com.cablemc.pokemoncobbled.common.client.util

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation

/**
 * Checks if a resource exists at this location
 */
fun ResourceLocation.exists(): Boolean {
    return Minecraft.getInstance().resourceManager.hasResource(this)
}