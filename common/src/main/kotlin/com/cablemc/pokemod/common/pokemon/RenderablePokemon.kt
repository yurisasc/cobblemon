/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon

import com.cablemc.pokemod.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemod.common.net.IntSize
import com.cablemc.pokemod.common.util.readSizedInt
import com.cablemc.pokemod.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

/**
 * A Pokémon that cannot be rendered on the client.
 *
 * @author Hiroku
 * @since August 1st, 2022
 */
data class RenderablePokemon(val species: Species, val aspects: Set<String>) {
    val form: FormData by lazy { species.getForm(aspects)!! }

    fun saveToBuffer(buffer: PacketByteBuf): PacketByteBuf {
        buffer.writeIdentifier(species.resourceIdentifier)
        buffer.writeSizedInt(IntSize.U_BYTE, aspects.size)
        aspects.forEach(buffer::writeString)
        return buffer
    }

    companion object {
        fun loadFromBuffer(buffer: PacketByteBuf): RenderablePokemon {
            val species = PokemonSpecies.getByIdentifier(buffer.readIdentifier())!!
            val aspects = mutableSetOf<String>()
            repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
                aspects.add(buffer.readString())
            }
            return RenderablePokemon(species, aspects)
        }
    }
}