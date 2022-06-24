package com.cablemc.pokemoncobbled.common.pokemon.evolution.variants

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

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

    override fun testContext(pokemon: Pokemon, context: Pokemon) = this.requiredContext.matches(context)

    override fun equals(other: Any?) = other is TradeEvolution && other.id.equals(this.id, true)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + ADAPTER_VARIANT.hashCode()
        return result
    }

    companion object {

        internal const val ADAPTER_VARIANT = "trade"

    }

}