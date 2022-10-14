/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.serverhandling.evolution

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.net.PacketHandler
import com.cablemc.pokemod.common.net.messages.server.pokemon.update.evolution.EvolutionDisplayUpdatePacket
import com.cablemc.pokemod.common.util.party
import com.cablemc.pokemod.common.util.runOnServer

class EvolutionDisplayUpdatePacketHandler<T : EvolutionDisplayUpdatePacket> : PacketHandler<T> {

    override fun invoke(packet: T, ctx: PokemodNetwork.NetworkContext) {
        runOnServer {
            // We only accept party evolutions
            ctx.player?.party()?.get(packet.pokemonID)?.let { pokemon -> packet.applyToPokemon(pokemon) }
        }
    }

}