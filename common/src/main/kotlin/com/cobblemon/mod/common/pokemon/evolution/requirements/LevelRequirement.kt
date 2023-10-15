/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.adapters.Variant
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

class LevelRequirement(val minLevel: Int = 1, val maxLevel: Int = Int.MAX_VALUE) : EvolutionRequirement {

    override fun check(pokemon: Pokemon) = pokemon.level in minLevel..maxLevel

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<LevelRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.INT.fieldOf("minLevel").forGetter(LevelRequirement::minLevel),
                Codec.INT.optionalFieldOf("maxLevel", Int.MAX_VALUE).forGetter(LevelRequirement::maxLevel),
            ).apply(builder, ::LevelRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("level"), CODEC)

    }

}