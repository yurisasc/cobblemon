/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.starter

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.asTranslated
import net.minecraft.text.Text

data class StarterCategory(
    val displayName: Text,
    val pokemon: List<PokemonProperties>
)

data class RenderableStarterCategory(
    val displayName: Text,
    val pokemon: List<RenderablePokemon>
)
