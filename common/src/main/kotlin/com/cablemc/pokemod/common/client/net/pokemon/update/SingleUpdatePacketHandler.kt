/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.pokemon.update

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.PokemonUpdatePacket

class SingleUpdatePacketHandler<T : PokemonUpdatePacket> : ClientPacketHandler<T> {
    override fun invokeOnClient(packet: T, ctx: PokemodNetwork.NetworkContext) {
        val pokemon = PokemodClient.storage.locatePokemon(packet.storeID, packet.pokemonID)
            ?: return // Ignore the update, it's not for a Pokémon we know about.
        packet.applyToPokemon(pokemon)
    }
}