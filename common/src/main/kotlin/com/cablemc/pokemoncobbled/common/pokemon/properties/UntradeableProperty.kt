package com.cablemc.pokemoncobbled.common.pokemon.properties

import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonPropertyType

/**
 * A type of [CustomPokemonPropertyType] handling a [FlagProperty] which, when
 * present, indicates that the Pokémon cannot be traded by any means.
 *
 * @author Hiroku
 * @since July 1st, 2022
 */
object UntradeableProperty : CustomPokemonPropertyType<FlagProperty> {
    override val keys = setOf("untradeable")
    override val needsKey = true

    override fun fromString(value: String?) =
        when {
            value == null || value.lowercase() in listOf("true", "yes") -> untradeable()
            value.lowercase() in listOf("false", "no") -> tradeable()
            else -> null
        }

    fun tradeable() = FlagProperty(keys.first(), true)
    fun untradeable() = FlagProperty(keys.first(), false)
}