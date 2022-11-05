/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.storage.pc

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.net.ClientPacketHandler
import com.cobblemon.mod.common.net.messages.client.storage.pc.MoveClientPCPokemonPacket

object MoveClientPCPokemonHandler : ClientPacketHandler<MoveClientPCPokemonPacket> {
    override fun invokeOnClient(packet: MoveClientPCPokemonPacket, ctx: CobblemonNetwork.NetworkContext) {
        CobblemonClient.storage.moveInPC(packet.storeID, packet.pokemonID, packet.newPosition)
    }
}