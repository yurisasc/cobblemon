/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.network.PacketByteBuf
import java.util.*

/**
 * Base packet for all the single-field Pokémon update packets.
 *
 * @author Hiroku
 * @since November 28th, 2021
 */
abstract class PokemonUpdatePacket : NetworkPacket {
    /** The UUID of the [PokemonStore] the Pokémon is in. */
    var storeID = UUID.randomUUID()
    /** The UUID of the [Pokemon] to update. */
    var pokemonID = UUID.randomUUID()

    fun setTarget(pokemon: Pokemon) {
        // This won't ever happen in instances where packets get sent out, but they protect us from NPEs on fields that require synchronization on load/save
        this.storeID = pokemon.storeCoordinates.get()?.store?.uuid ?: UUID.randomUUID()
        this.pokemonID = pokemon.uuid
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(storeID)
        buffer.writeUuid(pokemonID)
    }

    override fun decode(buffer: PacketByteBuf) {
        storeID = buffer.readUuid()
        pokemonID = buffer.readUuid()
    }

    /** Applies the update to the located Pokémon. */
    abstract fun applyToPokemon(pokemon: Pokemon)
}