/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.serverhandling.starter

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.net.messages.server.SelectStarterPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object SelectStarterPacketHandler : ServerPacketHandler<SelectStarterPacket> {
    override fun invokeOnServer(packet: SelectStarterPacket, ctx: CobbledNetwork.NetworkContext, player: ServerPlayerEntity) {
        PokemonCobbled.starterHandler.chooseStarter(player, packet.categoryName, packet.selected)
    }
}