/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.species.internal

import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.PreEvolution
import com.cobblemon.mod.common.api.pokemon.evolution.adapters.EvolutionRegistry
import com.cobblemon.mod.common.api.pokemon.species.Species
import com.cobblemon.mod.common.util.codec.setCodec
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.*

/**
 * Intermediate for [Species] data.
 */
internal data class EvolutionData(
    var evolutions: Set<Evolution>,
    var preEvolution: Optional<PreEvolution>,
) {

    companion object {

        val MAP_CODEC: MapCodec<EvolutionData> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                setCodec(EvolutionRegistry.EVOLUTION_CODEC).optionalFieldOf("evolutions", emptySet()).forGetter(EvolutionData::evolutions),
                PreEvolution.CODEC.optionalFieldOf("preEvolution").forGetter(EvolutionData::preEvolution)
            ).apply(builder, ::EvolutionData)
        }

        val CODEC: Codec<EvolutionData> = MAP_CODEC.codec()

    }

}
