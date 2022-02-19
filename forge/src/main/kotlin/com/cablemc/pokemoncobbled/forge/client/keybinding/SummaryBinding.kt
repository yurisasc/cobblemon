package com.cablemc.pokemoncobbled.forge.client.keybinding

import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.Summary
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object SummaryBinding : KeyMapping(
    "key.pokemoncobbled.summary",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_X,
    KeybindCategories.COBBLED_CATEGORY
) {
    var wasDown = false
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        if (this.isDown && !wasDown) {
            Minecraft.getInstance().setScreen(Summary(PokemonCobbledClient.storage.myParty))
            wasDown = true
        } else if (!this.isDown) {
            wasDown = false
        }
    }
}