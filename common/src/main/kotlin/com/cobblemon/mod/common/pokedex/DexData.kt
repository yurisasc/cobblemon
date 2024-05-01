/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.ClientDataSynchronizer
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class DexData (
    var identifier : Identifier,
    var contained_dexes : MutableList<Identifier> = mutableListOf(),
    var pokemon : MutableList<DexPokemonData> = mutableListOf()
): ClientDataSynchronizer<DexData> {
    override fun shouldSynchronize(other: DexData): Boolean {
        return other.identifier != identifier || other.contained_dexes != contained_dexes || other.pokemon != pokemon
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(identifier)
        buffer.writeInt(contained_dexes.size)
        contained_dexes.forEach {
            buffer.writeIdentifier(it)
        }
        buffer.writeInt(pokemon.size)
        pokemon.forEach {
            it.encode(buffer)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        identifier = buffer.readIdentifier()
        val subdexesSize = buffer.readInt()
        for (i in 0 until subdexesSize){
            contained_dexes.add(buffer.readIdentifier())
        }
        val pokemonSize = buffer.readInt()
        for (i in 0 until pokemonSize){
            val decodedPokemon = DexPokemonData()
            decodedPokemon.decode(buffer)
            pokemon.add(decodedPokemon)
        }
    }
}