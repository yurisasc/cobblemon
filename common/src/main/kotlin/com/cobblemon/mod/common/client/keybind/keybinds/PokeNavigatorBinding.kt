/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.keybind.keybinds

import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.summary.Summary
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinding
import com.cobblemon.mod.common.client.keybind.KeybindCategories
import com.cobblemon.mod.common.net.messages.server.starter.RequestStarterScreenPacket
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil

object PokeNavigatorBinding : CobblemonKeyBinding(
    "key.cobblemon.pokenavigator",
    InputUtil.Type.KEYSYM,
    InputUtil.GLFW_KEY_N,
    KeybindCategories.COBBLEMON_CATEGORY
) {
    override fun onPress() {
        val havePokemon = CobblemonClient.storage.myParty.slots.any { it != null }
        val starterSelected = CobblemonClient.clientPlayerData.starterSelected
        val startersLocked = CobblemonClient.clientPlayerData.starterLocked
        if (!starterSelected && !havePokemon) {
            if (startersLocked) {
                MinecraftClient.getInstance().player?.sendMessage(lang("ui.starter.cannotchoose").red(), false)
            } else {
                RequestStarterScreenPacket().sendToServer()
            }
        } else  {
//            MinecraftClient.getInstance().setScreen(PokeNav())
            MinecraftClient.getInstance().setScreen(Summary(CobblemonClient.storage.myParty))
        }
    }
}