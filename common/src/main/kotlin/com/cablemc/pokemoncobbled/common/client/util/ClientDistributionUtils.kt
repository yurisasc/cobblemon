package com.cablemc.pokemoncobbled.common.client.util

import net.minecraft.client.Minecraft
import net.minecraft.util.Identifier

/**
 * Checks if a resource exists at this location
 */
fun Identifier.exists(): Boolean {
    return MinecraftClient.getInstance().resourceManager.hasResource(this)
}