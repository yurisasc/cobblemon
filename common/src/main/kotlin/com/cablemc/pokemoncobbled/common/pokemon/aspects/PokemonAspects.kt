package com.cablemc.pokemoncobbled.common.pokemon.aspects

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.aspect.AspectProvider
import com.cablemc.pokemoncobbled.common.api.pokemon.aspect.SingleConditionalAspectProvider
import com.cablemc.pokemoncobbled.common.pokemon.Gender
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

val SHINY_ASPECT = object : SingleConditionalAspectProvider {
    override val aspect = "shiny"
    override fun meetsCondition(pokemon: Pokemon) = pokemon.shiny
    override fun meetsCondition(pokemonProperties: PokemonProperties) = pokemonProperties.shiny == true
}

val GENDER_ASPECT = object : AspectProvider {
    fun getFacetsForGender(gender: Gender) = setOf(
        when (gender) {
            Gender.MALE -> "male"
            Gender.FEMALE -> "female"
            Gender.GENDERLESS -> "genderless"
        }
    )

    override fun provide(pokemon: Pokemon) = getFacetsForGender(pokemon.gender)
    override fun provide(properties: PokemonProperties) = properties.gender?.let { getFacetsForGender(it) } ?: emptySet()
}

