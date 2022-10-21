/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.keybind.keybinds

import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.gui.pokenav.PokeNav
import com.cablemc.pokemod.common.client.keybind.KeybindCategories
import com.cablemc.pokemod.common.client.keybind.PokemodKeyBinding
import com.cablemc.pokemod.common.net.messages.server.starter.RequestStarterScreenPacket
import com.cablemc.pokemod.common.util.lang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

object PokeNavigatorBinding : PokemodKeyBinding(
    "key.pokemod.pokenavigator",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_N,
    KeybindCategories.POKEMOD_CATEGORY
) {
    override fun onPress() {
        val havePokemon = PokemodClient.storage.myParty.slots.any { it != null }
        val starterSelected = PokemodClient.clientPlayerData.starterSelected
        val startersLocked = PokemodClient.clientPlayerData.starterLocked
        if (!starterSelected && !havePokemon) {
            if (startersLocked) {
                MinecraftClient.getInstance().player?.sendMessage(lang("ui.starterscreen.cannotchoose").red(), false)
            } else {
                RequestStarterScreenPacket().sendToServer()
            }
        } else  {
            MinecraftClient.getInstance().setScreen(PokeNav())
        }
    }
}