package com.cablemc.pokemod.common.pokemon.properties

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.api.properties.CustomPokemonPropertyType

object PokemonLevelProperty : CustomPokemonPropertyType<IntProperty> {

    override val keys = setOf("level", "lvl", "l")
    override val needsKey = true

    override fun fromString(value: String?): IntProperty? {
        PokemonProperties
        val intValue = value?.toIntOrNull() ?: return null
        return IntProperty(
            key = this.keys.first(),
            value = intValue,
            pokemonApplicator = { pokemon, level -> pokemon.level = level },
            entityApplicator = { pokemonEntity, level -> pokemonEntity.pokemon.level = level },
            pokemonMatcher = { pokemon, level -> pokemon.level == level },
            entityMatcher = { pokemonEntity, level -> pokemonEntity.pokemon.level == level }
        )
    }

    override fun examples() = setOf("1", "${Pokemod.config.maxPokemonLevel}")
}