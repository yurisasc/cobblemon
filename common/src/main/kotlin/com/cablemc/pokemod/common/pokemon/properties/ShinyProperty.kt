package com.cablemc.pokemod.common.pokemon.properties

import com.cablemc.pokemod.common.api.properties.CustomPokemonPropertyType

object ShinyProperty : CustomPokemonPropertyType<BooleanProperty> {
    override val keys = setOf("shiny", "s")
    override val needsKey = true

    override fun fromString(value: String?): BooleanProperty? {
        val booleanValue = when (value?.lowercase()) {
            "true", "yes" -> true
            "false", "no" -> false
            else -> return null
        }
        return BooleanProperty(
            key = this.keys.first(),
            value = booleanValue,
            pokemonMatcher = { pokemon, isShiny -> pokemon.shiny == isShiny },
            entityMatcher = { pokemonEntity, isShiny -> pokemonEntity.pokemon.shiny == isShiny },
            pokemonApplicator = { pokemon, isShiny -> pokemon.shiny = isShiny },
            entityApplicator = { pokemonEntity, isShiny -> pokemonEntity.pokemon.shiny = isShiny }
        )
    }

    override fun examples() = setOf("yes", "no")
}