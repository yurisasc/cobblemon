/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.properties

import com.cablemc.pokemod.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemod.common.pokemon.Pokemon
class StringProperty(
    val key: String,
    val value: String,
    private val applicator: (pokemon: Pokemon, value: String) -> Unit,
    private val matcher: (pokemon: Pokemon, value: String) -> Boolean
) : CustomPokemonProperty {

    override fun apply(pokemon: Pokemon) {
        this.applicator.invoke(pokemon, this.value)
    }

    override fun matches(pokemon: Pokemon) = this.matcher.invoke(pokemon, this.value)

    override fun asString() = "${this.key}=${this.value}"

}