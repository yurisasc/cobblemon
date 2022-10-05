/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.data

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.pokemon.Species
import net.minecraft.network.PacketByteBuf

// We do not need to know every single attribute as a client, as such, we only sync the aspects that matter
class SpeciesRegistrySyncPacket : DataRegistrySyncPacket<Species>(PokemonSpecies.species) {

    override fun encodeEntry(buffer: PacketByteBuf, entry: Species) {
        try {
            entry.encodeForClient(buffer)
        } catch (e: Exception) {
            PokemonCobbled.LOGGER.error("Caught exception encoding the species {}", entry.resourceIdentifier, e)
        }
    }

    override fun decodeEntry(buffer: PacketByteBuf): Species? {
        val identifier = buffer.readIdentifier()
        val species = Species()
        species.resourceIdentifier = identifier
        return try {
            species.decodeForClient(buffer)
            species
        } catch (e: Exception) {
            PokemonCobbled.LOGGER.error("Caught exception decoding the species {}", identifier, e)
            null
        }
    }

    override fun synchronizeDecoded(entries: Collection<Species>) {
        PokemonSpecies.reload(entries.associateBy { it.resourceIdentifier })
    }
}