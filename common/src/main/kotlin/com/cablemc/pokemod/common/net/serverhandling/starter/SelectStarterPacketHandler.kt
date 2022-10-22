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
import com.cablemc.pokemod.common.net.messages.server.SelectStarterPacket
import com.cablemc.pokemod.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object SelectStarterPacketHandler : ServerPacketHandler<SelectStarterPacket> {
    override fun invokeOnServer(packet: SelectStarterPacket, ctx: PokemodNetwork.NetworkContext, player: ServerPlayerEntity) {
        Pokemod.starterHandler.chooseStarter(player, packet.categoryName, packet.selected)
    }
}