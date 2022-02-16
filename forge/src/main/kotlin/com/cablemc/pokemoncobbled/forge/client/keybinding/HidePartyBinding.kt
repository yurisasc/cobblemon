package com.cablemc.pokemoncobbled.forge.client.keybinding

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object HidePartyBinding : KeyMapping(
    "key.pokemoncobbled.hideparty",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_P,
    KeybindCategories.COBBLED_CATEGORY
) {
    var wasDown = false
    var shouldHide = false
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (this.isDown && !wasDown) {
            shouldHide = !shouldHide
            wasDown = true
        } else if (!this.isDown) {
            wasDown = false
        }
    }
}