/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.net.storage.pc

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.SetPCPokemonPacket

object SetPCPokemonHandler : ClientPacketHandler<SetPCPokemonPacket> {
    override fun invokeOnClient(packet: SetPCPokemonPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.storage.setPCPokemon(packet.storeID, packet.storePosition, packet.pokemon)
    }
}