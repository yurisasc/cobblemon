/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.evolution.adapters.Variant
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.progress.UseMoveEvolutionProgress
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * An [EvolutionRequirement] meant to require a move to have been used a specific amount of times.
 *
 * @param move The [MoveTemplate] expected to be used.
 * @param amount The amount of times it has been used.
 *
 * @author Licious
 * @since January 25th, 2023
 */
class UseMoveRequirement(val move: MoveTemplate, val amount: Int) : EvolutionRequirement {

    override fun check(pokemon: Pokemon): Boolean = pokemon.evolutionProxy.current()
        .progress()
        .filterIsInstance<UseMoveEvolutionProgress>()
        .any { progress -> progress.currentProgress().move == this.move && progress.currentProgress().amount >= this.amount }

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<UseMoveRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                MoveTemplate.CODEC.fieldOf("move").forGetter(UseMoveRequirement::move),
                Codec.INT.optionalFieldOf("amount", 1).forGetter(UseMoveRequirement::amount)
            ).apply(builder, ::UseMoveRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("use_move"), CODEC)

    }

}