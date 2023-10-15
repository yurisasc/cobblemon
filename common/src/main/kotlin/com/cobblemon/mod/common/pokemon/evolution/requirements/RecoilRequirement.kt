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
import com.cobblemon.mod.common.pokemon.evolution.progress.RecoilEvolutionProgress
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * An [EvolutionRequirement] which requires a specific [amount] of recoil without fainting in order to pass.
 * It keeps track of progress through [RecoilEvolutionProgress].
 *
 * @param amount The requirement amount of recoil
 *
 * @author Licious
 * @since January 27th, 2022
 */
class RecoilRequirement(val amount: Int) : EvolutionRequirement {

    override fun check(pokemon: Pokemon): Boolean = pokemon.evolutionProxy.current()
        .progress()
        .filterIsInstance<RecoilEvolutionProgress>()
        .any { progress -> progress.currentProgress().recoil >= this.amount }

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<RecoilRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.INT.fieldOf("amount").forGetter(RecoilRequirement::amount)
            ).apply(builder, ::RecoilRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("recoil"), CODEC)

    }

}