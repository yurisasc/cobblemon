package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * An [EvolutionRequirement] for when the [Pokemon] must match [PokemonProperties.matches].
 *
 * @property target The matcher for this requirement.
 * @author Licious
 * @since March 26th, 2022
 */
class PokemonPropertiesRequirement(val target: PokemonProperties) : EvolutionRequirement {

    override fun check(pokemon: Pokemon) = this.target.matches(pokemon)

    companion object {

        internal const val ADAPTER_VARIANT = "properties"

    }

}