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
import com.cablemc.pokemod.common.net.messages.client.storage.RemoveClientPokemonPacket


object RemoveClientPokemonHandler : ClientPacketHandler<RemoveClientPokemonPacket> {
    override fun invokeOnClient(packet: RemoveClientPokemonPacket, ctx: PokemodNetwork.NetworkContext) {
        if (packet.storeIsParty) {
            PokemodClient.storage.removeFromParty(packet.storeID, packet.pokemonID)
        } else {
            PokemodClient.storage.removeFromPC(packet.storeID, packet.pokemonID)
        }
    }
}