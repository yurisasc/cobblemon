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
import com.cobblemon.mod.common.pokemon.feature.BattleCriticalHitsFeature
class BattleCriticalHitsRequirement : EvolutionRequirement {
    companion object {
        const val ADAPTER_VARIANT = BattleCriticalHitsFeature.ID
    }

    val amount = 0
    override fun check(pokemon: Pokemon): Boolean {
        val feature = pokemon.getFeature<BattleCriticalHitsFeature>(BattleCriticalHitsFeature.ID) ?: return false
        return feature.currentValue >= this.amount
    }

}