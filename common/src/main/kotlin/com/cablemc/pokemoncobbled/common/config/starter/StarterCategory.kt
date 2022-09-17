/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.config.starter

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.pokemon.RenderablePokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated

data class StarterCategory(
    val name: String,
    val displayName: String,
    val pokemon: List<PokemonProperties>
) {
    fun asRenderableStarterCategory() = RenderableStarterCategory(name, displayName, pokemon.map { it.asRenderablePokemon() })
}

data class RenderableStarterCategory(
    val name: String,
    val displayName: String,
    val pokemon: List<RenderablePokemon>
) {
    val displayNameText = displayName.asTranslated()
}
