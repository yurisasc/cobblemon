/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cablemc.pokemoncobbled.common.pokemon.Gender
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.util.asIdentifierDefaultingNamespace
import net.minecraft.text.MutableText
import java.util.UUID

class ClientBattlePokemon(
    val uuid: UUID,
    var displayName: MutableText,
    var properties: PokemonProperties,
    var hpRatio: Float,
    var status: PersistentStatus?,
    var statChanges: MutableMap<Stat, Int>
) {
    lateinit var actor: ClientBattleActor
    val species: Species
        get() = PokemonSpecies.getByIdentifier(properties.species!!.asIdentifierDefaultingNamespace())!!
    val level: Int
        get() = properties.level ?: 0

    val gender: Gender
        get() = properties.gender ?: Gender.GENDERLESS

    var state = PokemonFloatingState()
}