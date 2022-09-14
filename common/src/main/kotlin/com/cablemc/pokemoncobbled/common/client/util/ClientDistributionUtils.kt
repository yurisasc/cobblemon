package com.cablemc.pokemoncobbled.common.client.util

import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier

/**
 * Checks if a resource exists at this location
 */
fun Identifier.exists(): Boolean {
    return MinecraftClient.getInstance().resourceManager.getResource(this).isPresent
}

fun runOnRender(action: () -> Unit) {
    MinecraftClient.getInstance().execute(action)
}