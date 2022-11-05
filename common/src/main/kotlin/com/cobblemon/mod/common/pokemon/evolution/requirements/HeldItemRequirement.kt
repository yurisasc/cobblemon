/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
class HeldItemRequirement : EvolutionRequirement {

    // ToDo Pending impl of held items, this is here because stat scrapper already accounts for this to exist
    override fun check(pokemon: Pokemon): Boolean = true

    companion object {
        const val ADAPTER_VARIANT = "held_item"
    }
}