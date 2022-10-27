/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution.requirements

import com.cablemc.pokemod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemod.common.api.types.ElementalType
import com.cablemc.pokemod.common.api.types.ElementalTypes
import com.cablemc.pokemod.common.pokemon.Pokemon
class MoveTypeRequirement : EvolutionRequirement {
    val type: ElementalType = ElementalTypes.NORMAL
    override fun check(pokemon: Pokemon) = pokemon.moveSet.getMoves().any { move -> move.type == type }
    companion object {
        const val ADAPTER_VARIANT = "has_move_type"
    }
}