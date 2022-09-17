/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * An [EvolutionRequirement] for when a certain amount of [Pokemon.friendship] is expected.
 *
 * @property amount The required [Pokemon.friendship] amount, expects between 0 & 255.
 * @author Licious
 * @since March 21st, 2022
 */
class FriendshipRequirement : EvolutionRequirement {
    val amount = 0
    override fun check(pokemon: Pokemon) = pokemon.friendship >= this.amount

    companion object {
        const val ADAPTER_VARIANT = "friendship"
    }
}