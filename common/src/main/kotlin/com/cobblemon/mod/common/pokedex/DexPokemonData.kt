/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.pokedex.adapter.DexPokemonDataAdapter
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class DexPokemonData(
    var name : Identifier = cobblemonResource("dex.pokemon"),
    var forms : MutableList<String> = mutableListOf()
): Decodable, Encodable {

    val species : Species?
        get() = PokemonSpecies.getByIdentifier(name)

    fun combine(diffDexPokemonData: DexPokemonData): DexPokemonData {
        val newForms: MutableList<String> = mutableListOf()
        newForms.addAll(forms)
        newForms.addAll(diffDexPokemonData.forms)

        return DexPokemonData(name, newForms)
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(name)
        buffer.writeInt(forms.size)
        forms.forEach {
            buffer.writeString(it)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        name = buffer.readIdentifier()
        val formsOrderListSize = buffer.readInt()
        for(i in 0 until formsOrderListSize){
            forms.add(buffer.readString())
        }
    }
}