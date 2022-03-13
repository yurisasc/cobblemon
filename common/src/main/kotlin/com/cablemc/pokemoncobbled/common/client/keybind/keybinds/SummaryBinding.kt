package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyMapping
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft

object SummaryBinding : CobbledKeyMapping(
    "key.pokemoncobbled.summary",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_X,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        Minecraft.getInstance().setScreen(Summary(PokemonCobbledClient.storage.myParty))
    }
}