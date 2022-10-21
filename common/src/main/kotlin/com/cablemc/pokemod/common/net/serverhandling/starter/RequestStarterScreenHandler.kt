/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling.starter

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.net.messages.server.starter.RequestStarterScreenPacket
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler
import com.cablemc.pokemod.common.util.lang
import net.minecraft.server.network.ServerPlayerEntity

object RequestStarterScreenHandler : ServerPacketHandler<RequestStarterScreenPacket> {
    override fun invokeOnServer(packet: RequestStarterScreenPacket, ctx: PokemodNetwork.NetworkContext, player: ServerPlayerEntity) {
        val playerData = Pokemod.playerData.get(player)

        if (playerData.starterSelected) {
            return player.sendMessage(lang("ui.starter.alreadyselected").red())
        } else if (playerData.starterLocked) {
            return player.sendMessage(lang("ui.starter.cannotchoose").red())
        } else {
            Pokemod.starterHandler.requestStarterChoice(player)
        }
    }
}