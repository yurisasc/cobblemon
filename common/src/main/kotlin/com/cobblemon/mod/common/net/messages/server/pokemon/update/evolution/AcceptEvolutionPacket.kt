/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.server.pokemon.update.evolution

import com.cobblemon.mod.common.api.pokemon.evolution.EvolutionDisplay
import com.cobblemon.mod.common.net.messages.client.pokemon.update.SingleUpdatePacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.CobblemonEvolutionDisplay
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class AcceptEvolutionPacket(pokemon: Pokemon, value: EvolutionDisplay) : SingleUpdatePacket<EvolutionDisplay, AcceptEvolutionPacket>(pokemon, value) {

    override val id = ID

    override fun encodeValue(buffer: PacketByteBuf) {
        buffer.writeString(this.value.id)
    }

    override fun set(pokemon: Pokemon, value: EvolutionDisplay) {
        val evolution = pokemon.evolutionProxy.server().firstOrNull { evolution -> evolution.id.equals(value.id, true) } ?: return
        pokemon.evolutionProxy.server().start(evolution)
    }

    companion object {
        val ID = cobblemonResource("accept_evolution")
        fun decode(buffer: PacketByteBuf): AcceptEvolutionPacket {
            val pokemon = decodePokemon(buffer)
            // Doesn't matter we just want the ID
            val display = CobblemonEvolutionDisplay(buffer.readString(), pokemon)
            return AcceptEvolutionPacket(pokemon, display)
        }
    }
}