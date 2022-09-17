/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * An [EvolutionRequirement] for when a certain [MoveTemplate] is expected in the [Pokemon.moveSet].
 *
 * @property move The required [MoveTemplate].
 * @author Licious
 * @since March 21st, 2022
 */
class MoveSetRequirement : EvolutionRequirement {
    val move: MoveTemplate = Moves.getByNameOrDummy("tackle")
    override fun check(pokemon: Pokemon) = pokemon.moveSet.getMoves().any { move -> move.name.equals(this.move.name, true) }
    companion object {
        const val ADAPTER_VARIANT = "has_move"
    }
}