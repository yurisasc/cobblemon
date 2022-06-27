package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * An [EvolutionRequirement] for when the party needs to either contain or not a specific match for [target] based on the [contains] property.
 *
 * @property target The matcher for the party members.
 * @property contains If this requirement will need the [target] to be present or not.
 * @author Licious
 * @since March 21st, 2022
 */
class PartyMemberRequirement(val target: PokemonProperties, val contains: Boolean) : EvolutionRequirement {

    override fun check(pokemon: Pokemon): Boolean {
        val party = pokemon.storeCoordinates.get()?.store as? PartyStore ?: return false
        if (party.getAll().any { member -> member.uuid != pokemon.uuid && this.target.matches(member) })
            return this.contains
        return !this.contains
    }

    companion object {

        internal const val ADAPTER_VARIANT = "party_member"

    }

}