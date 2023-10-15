/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.species.internal

import com.cobblemon.mod.common.api.pokemon.species.Species
import com.cobblemon.mod.common.pokemon.ai.PokemonBehaviour
import com.cobblemon.mod.common.util.codec.setCodec
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * Intermediate for [Species] data.
 */
internal data class BehaviourData(
    var pokemonBehaviour: PokemonBehaviour,
    var shoulderMountable: Boolean,
    var features: Set<String>,
    var dynamaxBlocked: Boolean,
) {

    companion object {

        val MAP_CODEC: MapCodec<BehaviourData> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                PokemonBehaviour.CODEC.fieldOf("pokemonBehaviour").forGetter(BehaviourData::pokemonBehaviour),
                Codec.BOOL.fieldOf("shoulderMountable").forGetter(BehaviourData::shoulderMountable),
                setCodec(Codec.STRING).fieldOf("features").forGetter(BehaviourData::features),
                Codec.BOOL.optionalFieldOf("dynamaxBlocked", false).forGetter(BehaviourData::dynamaxBlocked)
            ).apply(builder, ::BehaviourData)
        }

        val CODEC: Codec<BehaviourData> = MAP_CODEC.codec()

    }

}
