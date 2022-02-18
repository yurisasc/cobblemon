package com.cablemc.pokemoncobbled.common.client.keybind.keybinds

import com.cablemc.pokemoncobbled.common.client.gui.pokenav.PokeNav
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeyMapping
import com.cablemc.pokemoncobbled.common.client.keybind.KeybindCategories
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft

object PokeNavigatorBinding : CobbledKeyMapping(
    "key.pokemoncobbled.pokenavigator",
    InputConstants.Type.KEYSYM,
    InputConstants.KEY_N,
    KeybindCategories.COBBLED_CATEGORY
) {
    override fun onPress() {
        Minecraft.getInstance().setScreen(PokeNav())
    }
}