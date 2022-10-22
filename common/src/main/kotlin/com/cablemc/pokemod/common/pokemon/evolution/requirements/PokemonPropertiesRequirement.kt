/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution.requirements

import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemod.common.pokemon.Pokemon

/**
 * An [EvolutionRequirement] for when the [Pokemon] must match [PokemonProperties.matches].
 *
 * @property target The matcher for this requirement.
 * @author Licious
 * @since March 26th, 2022
 */
class PokemonPropertiesRequirement : EvolutionRequirement {
    val target = PokemonProperties()
    override fun check(pokemon: Pokemon) = this.target.matches(pokemon)

    companion object {
        const val ADAPTER_VARIANT = "properties"
    }
}