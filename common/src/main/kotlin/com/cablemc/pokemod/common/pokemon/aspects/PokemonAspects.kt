/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.aspects

import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.api.pokemon.aspect.AspectProvider
import com.cablemc.pokemod.common.api.pokemon.aspect.SingleConditionalAspectProvider
import com.cablemc.pokemod.common.pokemon.Gender
import com.cablemc.pokemod.common.pokemon.Pokemon

val SHINY_ASPECT = object : SingleConditionalAspectProvider {
    override val aspect = "shiny"
    override fun meetsCondition(pokemon: Pokemon) = pokemon.shiny
    override fun meetsCondition(pokemonProperties: PokemonProperties) = pokemonProperties.shiny == true
}

val GENDER_ASPECT = object : AspectProvider {
    fun getFacetsForGender(gender: Gender) = setOf(
        when (gender) {
            Gender.MALE -> "male"
            Gender.FEMALE -> "female"
            Gender.GENDERLESS -> "genderless"
        }
    )

    override fun provide(pokemon: Pokemon) = getFacetsForGender(pokemon.gender)
    override fun provide(properties: PokemonProperties) = properties.gender?.let { getFacetsForGender(it) } ?: emptySet()
}
