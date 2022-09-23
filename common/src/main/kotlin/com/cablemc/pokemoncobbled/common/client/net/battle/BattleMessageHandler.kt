/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.text.bold
import com.cablemc.pokemoncobbled.common.api.text.font
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.battle.widgets.BattleMessagePane
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleMessagePacket
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Language

object BattleMessageHandler : ClientPacketHandler<BattleMessagePacket> {
    override fun invokeOnClient(packet: BattleMessagePacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
        val textRenderer = MinecraftClient.getInstance().textRenderer
        for (message in packet.messages) {
            val line = message.copy().bold().font(CobbledResources.DEFAULT_LARGE)
            val lines = Language.getInstance().reorder(textRenderer.textHandler.wrapLines(line, BattleMessagePane.LINE_WIDTH, line.style))
            battle.messages.add(lines)
        }
    }
}