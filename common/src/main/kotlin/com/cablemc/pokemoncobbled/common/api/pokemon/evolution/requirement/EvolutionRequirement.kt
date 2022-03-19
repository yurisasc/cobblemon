package com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

fun interface EvolutionRequirement {

    fun check(pokemon: Pokemon): Boolean

}