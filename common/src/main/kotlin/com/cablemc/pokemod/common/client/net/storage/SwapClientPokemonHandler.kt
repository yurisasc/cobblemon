/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.storage

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.storage.SwapClientPokemonPacket

object SwapClientPokemonHandler : ClientPacketHandler<SwapClientPokemonPacket> {
    override fun invokeOnClient(packet: SwapClientPokemonPacket, ctx: PokemodNetwork.NetworkContext) {
        if (packet.storeIsParty) {
            PokemodClient.storage.swapInParty(packet.storeID, packet.pokemonID1, packet.pokemonID2)
        } else {
            PokemodClient.storage.swapInPC(packet.storeID, packet.pokemonID1, packet.pokemonID2)
        }
    }
}