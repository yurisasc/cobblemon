package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class HeldItemRequirement : EvolutionRequirement {

    override fun check(pokemon: Pokemon): Boolean {
        TODO("Not yet implemented")
    }

    companion object {

        internal const val ADAPTER_VARIANT = "held_item"

    }

}