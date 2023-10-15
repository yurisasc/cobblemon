/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.variants

import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.PassiveEvolution
import com.cobblemon.mod.common.api.pokemon.evolution.adapters.Variant
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * Represents a [PassiveEvolution].
 * This can be triggered at any check as long as the [Pokemon] passes [LevelUpEvolution.isValid].
 *
 * @property levels The level range the [Pokemon] is expected to be in, if the range only has a single number the [Pokemon.level] will need to be equal or greater then it instead.
 * @author Licious
 * @since March 20th, 2022
 */
open class LevelUpEvolution(
    override val id: String,
    override val result: PokemonProperties,
    override var optional: Boolean,
    override var consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>
) : PassiveEvolution {

    override val variant: Variant<Evolution> = MAIN_VARIANT

    override fun equals(other: Any?) = other is LevelUpEvolution && other.id.equals(this.id, true)

    override fun hashCode(): Int {
        var result = this.id.hashCode()
        result = 31 * result + this.variant.identifier.hashCode()
        return result
    }

    companion object {

        val CODEC: Codec<LevelUpEvolution> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.STRING.fieldOf("id").forGetter(LevelUpEvolution::id),
                PokemonProperties.CODEC.fieldOf("result").forGetter(LevelUpEvolution::result),
                Codec.BOOL.optionalFieldOf("optional", true).forGetter(LevelUpEvolution::optional),
                Codec.BOOL.optionalFieldOf("consumeHeldItem", false).forGetter(LevelUpEvolution::optional),
                Codec.list(EvolutionRequirement.CODEC).optionalFieldOf("requirements", mutableListOf()).xmap({ it.toMutableSet() }, { it.toMutableList() }).forGetter(LevelUpEvolution::requirements),
                Codec.list(MoveTemplate.CODEC).optionalFieldOf("learnableMoves", mutableListOf()).xmap({ it.toMutableSet() }, { it.toMutableList() }).forGetter(LevelUpEvolution::learnableMoves)
            ).apply(builder, ::LevelUpEvolution)
        }

        internal val MAIN_VARIANT: Variant<Evolution> = Variant(cobblemonResource("level_up"), CODEC)
        // Just for user convenience sake as we may have passive evolutions not backed by level ups
        internal val ALTERNATIVE_VARIANT: Variant<Evolution> = Variant(cobblemonResource("passive"), CODEC)

    }
}