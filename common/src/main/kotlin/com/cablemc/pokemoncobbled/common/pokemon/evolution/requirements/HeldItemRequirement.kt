package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class HeldItemRequirement : EvolutionRequirement {

    // ToDo Pending impl of held items, this is here because stat scrapper already accounts for this to exist
    override fun check(pokemon: Pokemon): Boolean = true

    companion object {
        const val ADAPTER_VARIANT = "held_item"
    }
}