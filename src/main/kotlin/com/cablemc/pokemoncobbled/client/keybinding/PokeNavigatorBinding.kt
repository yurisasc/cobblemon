package com.cablemc.pokemoncobbled.client.keybinding

import com.cablemc.pokemoncobbled.client.gui.PokeNav
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PokeNavigatorBinding : KeyMapping(
    "key.pokemoncobbled.pokenavigator",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_C,
    CATEGORY_GAMEPLAY
) {
    var wasDown = false
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (this.isDown && !wasDown) {
            Minecraft.getInstance().setScreen(PokeNav())
            wasDown = true
        } else if (!this.isDown) {
            wasDown = false
        }
    }
}