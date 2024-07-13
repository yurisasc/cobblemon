/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.npc.partyproviders

import com.cobblemon.mod.common.api.npc.NPCPartyProvider
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.google.gson.JsonElement

class SimplePartyProvider : NPCPartyProvider {
    companion object {
        const val TYPE = "simple"
    }

    @Transient
    override val type = TYPE

    val pokemon = mutableListOf<PokemonProperties>()

    override fun loadFromJSON(json: JsonElement) {
        val pokemonList = json.asJsonObject.getAsJsonArray("pokemon")
        pokemonList.forEach {
            val obj = it.asJsonObject
            val pokemon = PokemonProperties()
            pokemon.loadFromJSON(obj)
            this.pokemon.add(pokemon)
        }
    }

    override fun provide(npc: NPCEntity): NPCParty {
        val pokemon = pokemon.map { it.create() }
        val party = PartyStore(npc.uuid)
        pokemon.forEach(party::add)
        return StaticNPCParty(party)
    }
}