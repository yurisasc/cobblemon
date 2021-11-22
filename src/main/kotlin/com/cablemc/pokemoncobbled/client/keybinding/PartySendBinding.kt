package com.cablemc.pokemoncobbled.client.keybinding

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.lwjgl.glfw.GLFW

object PartySendBinding : KeyMapping(
    "key.pokemoncobbled.throwpartypokemon",
    KeyConflictContext.UNIVERSAL,
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_R,
    "key.categories.pokemoncobbled"
) {
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        println("Key down!")
        if (isDown) {
            val selectedSlot = "" // stuff
        }
    }
}