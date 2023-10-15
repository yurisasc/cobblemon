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
import com.cobblemon.mod.common.api.pokemon.evolution.ContextEvolution
import com.cobblemon.mod.common.api.pokemon.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.evolution.adapters.Variant
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * Represents a [ContextEvolution] with [Pokemon] context.
 * This is triggered by trading.
 * The context is the received [Pokemon] from the trade.
 *
 * @property requiredContext The [PokemonProperties] representation of the expected received [Pokemon] from the trade.
 * @author Licious
 * @since March 20th, 2022
 */
open class TradeEvolution(
    override val id: String,
    override val result: PokemonProperties,
    override val requiredContext: PokemonProperties,
    override var optional: Boolean,
    override var consumeHeldItem: Boolean,
    override val requirements: MutableSet<EvolutionRequirement>,
    override val learnableMoves: MutableSet<MoveTemplate>
) : ContextEvolution<Pokemon, PokemonProperties> {

    override val variant: Variant<Evolution> = VARIANT

    override fun testContext(pokemon: Pokemon, context: Pokemon) = this.requiredContext.matches(context)

    override fun equals(other: Any?) = other is TradeEvolution && other.id.equals(this.id, true)

    override fun hashCode(): Int {
        var result = this.id.hashCode()
        result = 31 * result + this.variant.identifier.hashCode()
        return result
    }

    companion object {

        val CODEC: Codec<TradeEvolution> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.STRING.fieldOf("id").forGetter(TradeEvolution::id),
                PokemonProperties.CODEC.fieldOf("result").forGetter(TradeEvolution::result),
                PokemonProperties.CODEC.fieldOf("requiredContext").forGetter(TradeEvolution::requiredContext),
                Codec.BOOL.optionalFieldOf("optional", true).forGetter(TradeEvolution::optional),
                Codec.BOOL.optionalFieldOf("consumeHeldItem", false).forGetter(TradeEvolution::optional),
                Codec.list(EvolutionRequirement.CODEC).optionalFieldOf("requirements", mutableListOf()).xmap({ it.toMutableSet() }, { it.toMutableList() }).forGetter(TradeEvolution::requirements),
                Codec.list(MoveTemplate.CODEC).optionalFieldOf("learnableMoves", mutableListOf()).xmap({ it.toMutableSet() }, { it.toMutableList() }).forGetter(TradeEvolution::learnableMoves)
            ).apply(builder, ::TradeEvolution)
        }

        internal val VARIANT: Variant<Evolution> = Variant(cobblemonResource("trade"), CODEC)

    }

}