/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.battle

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.text.bold
import com.cablemc.pokemod.common.api.text.font
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.PokemodResources
import com.cablemc.pokemod.common.client.gui.battle.widgets.BattleMessagePane
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.BattleMessagePacket
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Language

object BattleMessageHandler : ClientPacketHandler<BattleMessagePacket> {
    override fun invokeOnClient(packet: BattleMessagePacket, ctx: PokemodNetwork.NetworkContext) {
        val battle = PokemodClient.battle ?: return
        val textRenderer = MinecraftClient.getInstance().textRenderer
        for (message in packet.messages) {
            val line = message.copy().bold().font(PokemodResources.DEFAULT_LARGE)
            val lines = Language.getInstance().reorder(textRenderer.textHandler.wrapLines(line, BattleMessagePane.LINE_WIDTH, line.style))
            battle.messages.add(lines)
        }
    }
}