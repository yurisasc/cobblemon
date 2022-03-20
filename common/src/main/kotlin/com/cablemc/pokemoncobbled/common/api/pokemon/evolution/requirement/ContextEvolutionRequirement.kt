package com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

fun interface ContextEvolutionRequirement<T> {

    fun check(pokemon: Pokemon, context: T): Boolean

}