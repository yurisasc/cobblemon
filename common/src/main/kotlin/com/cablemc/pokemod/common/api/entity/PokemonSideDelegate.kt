/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.entity

import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.pokemon.Pokemon
import net.minecraft.entity.damage.DamageSource

interface PokemonSideDelegate : EntitySideDelegate<PokemonEntity> {
    fun changePokemon(pokemon: Pokemon)
    fun drop(source: DamageSource?) {}
    fun updatePostDeath() {}
}