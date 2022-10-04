/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.data

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.google.gson.reflect.TypeToken
import net.minecraft.util.Identifier

class SpeciesRegistrySyncPacket : JsonDataRegistrySyncPacket<Species>(PokemonSpecies.gson, PokemonSpecies.species) {
    override fun synchronizeDecoded(entries: Map<Identifier, Species>) {
        PokemonSpecies.reload(entries)
    }

    override fun type(): TypeToken<Species> = TypeToken.get(Species::class.java)
}