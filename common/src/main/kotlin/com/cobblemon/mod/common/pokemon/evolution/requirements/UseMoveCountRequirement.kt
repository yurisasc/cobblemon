/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.feature.UseMoveCountFeature

/**
 * An [EvolutionRequirement] meant to require a move to have been used a specific amount of times.
 *
 * @param move The [MoveTemplate] expected to be used.
 * @param amount The amount of times it has been used.
 *
 * @author Licious
 * @since January 25th, 2023
 */
class UseMoveCountRequirement(move: MoveTemplate, amount: Int) : EvolutionRequirement {

    constructor() : this(Moves.getByNumericalId(0), 1)

    val move: MoveTemplate = move
    val amount: Int = amount

    override fun check(pokemon: Pokemon): Boolean {
        val feature = pokemon.getFeature<UseMoveCountFeature>(UseMoveCountFeature.ID) ?: return false
        return feature.amount(this.move) >= this.amount
    }

    companion object {
        const val ADAPTER_VARIANT = UseMoveCountFeature.ID
    }

}