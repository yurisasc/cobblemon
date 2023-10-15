/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.adapters.EvolutionRegistry
import com.cobblemon.mod.common.api.pokemon.evolution.adapters.Variant
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

class NoneOfRequirement(val requirements: Collection<EvolutionRequirement>) : EvolutionRequirement {

    override fun check(pokemon: Pokemon): Boolean = this.requirements.none { it.check(pokemon) }

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<NoneOfRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.list(EvolutionRegistry.REQUIREMENT_CODEC).fieldOf("requirements").forGetter { it.requirements.toMutableList() }
            ).apply(builder, ::NoneOfRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("none_of"), CODEC)

    }

}