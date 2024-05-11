/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.api.data.ClientDataSynchronizer
import com.cobblemon.mod.common.api.pokedex.PokedexEntryCategory
import com.cobblemon.mod.common.api.pokedex.PokedexJSONRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class DexData (
    var identifier : Identifier,
    var enabled : Boolean = true,
    var contained_dexes : MutableList<Identifier> = mutableListOf(),
    var pokemon_list : MutableList<DexPokemonData> = mutableListOf(),
    var override_categories : Boolean = false
): ClientDataSynchronizer<DexData> {
    override fun shouldSynchronize(other: DexData): Boolean {
        return other.identifier != identifier || other.contained_dexes != contained_dexes || other.pokemon_list != pokemon_list
    }

    fun parseEntries(entries: MutableSet<DexPokemonData>, categoryEntries : MutableMap<PokedexEntryCategory, MutableSet<DexPokemonData>>){
        if (!enabled) return

        for(pokemon in pokemon_list){
            if(override_categories){
                entries.add(pokemon)
            } else {
                categoryEntries[pokemon.category]!!.add(pokemon)
            }
        }

        for(childDexIdentifier in contained_dexes){
            PokedexJSONRegistry.dexByIdentifier
            PokedexJSONRegistry.getByIdentifier(childDexIdentifier)!!.parseEntries(entries, categoryEntries)
        }
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(identifier)
        buffer.writeBoolean(override_categories)
        buffer.writeBoolean(enabled)
        buffer.writeInt(contained_dexes.size)
        contained_dexes.forEach {
            buffer.writeIdentifier(it)
        }
        buffer.writeInt(pokemon_list.size)
        pokemon_list.forEach {
            it.encode(buffer)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        identifier = buffer.readIdentifier()
        override_categories = buffer.readBoolean()
        enabled = buffer.readBoolean()
        val subdexesSize = buffer.readInt()
        for (i in 0 until subdexesSize){
            contained_dexes.add(buffer.readIdentifier())
        }
        val pokemonSize = buffer.readInt()
        for (i in 0 until pokemonSize){
            val decodedPokemon = DexPokemonData()
            decodedPokemon.decode(buffer)
            pokemon_list.add(decodedPokemon)
        }
    }
}