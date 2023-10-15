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
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.codec.ExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

class MoveTypeRequirement(val type: ElementalType) : EvolutionRequirement {

    override fun check(pokemon: Pokemon) = pokemon.moveSet.getMoves().any { move -> move.type == type }

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<MoveTypeRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                ExtraCodecs.createRegistryElementCodec { CobblemonRegistries.ELEMENTAL_TYPE }.fieldOf("type").forGetter(MoveTypeRequirement::type)
            ).apply(builder, ::MoveTypeRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("has_move_type"), CODEC)

    }
}