/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.serverhandling.evolution

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.pokemon.update.evolution.EvolutionDisplayUpdatePacket
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.runOnServer

class EvolutionDisplayUpdatePacketHandler<T : EvolutionDisplayUpdatePacket> : PacketHandler<T> {

    override fun invoke(packet: T, ctx: CobbledNetwork.NetworkContext) {
        runOnServer {
            // We only accept party evolutions
            ctx.player?.party()?.get(packet.pokemonID)?.let { pokemon -> packet.applyToPokemon(pokemon) }
        }
    }

}