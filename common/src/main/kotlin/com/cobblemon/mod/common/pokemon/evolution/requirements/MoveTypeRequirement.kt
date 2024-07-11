/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.registry.CobblemonRegistries
import net.minecraft.util.RandomSource

class MoveTypeRequirement : EvolutionRequirement {
    val type: ElementalType = CobblemonRegistries.ELEMENTAL_TYPE.getRandom(RandomSource.create()).get().value()
    override fun check(pokemon: Pokemon) = pokemon.moveSet.getMoves().any { move -> move.type == type }
    companion object {
        const val ADAPTER_VARIANT = "has_move_type"
    }
}