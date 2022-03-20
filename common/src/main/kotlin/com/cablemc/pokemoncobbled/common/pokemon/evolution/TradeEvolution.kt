package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.ContextEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.Species

open class TradeEvolution(
    override val id: String,
    override val result: PokemonProperties,
    override val requiredContext: TradePartner,
    override val optional: Boolean,
    override val consumeHeldItem: Boolean,
    override val requirements: List<EvolutionRequirement>
) : ContextEvolution<TradeEvolution.TradePartner> {

    override fun testContext(pokemon: Pokemon, context: TradePartner) = context.species == this.requiredContext.species

    data class TradePartner(val species: Species) {

        constructor(pokemon: Pokemon) : this(pokemon.species)

    }

    companion object {

        internal const val ADAPTER_VARIANT = "trade"

    }

}