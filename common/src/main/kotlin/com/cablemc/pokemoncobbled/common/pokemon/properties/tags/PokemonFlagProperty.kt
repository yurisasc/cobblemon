package com.cablemc.pokemoncobbled.common.pokemon.properties.tags

import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonPropertyType
import com.cablemc.pokemoncobbled.common.pokemon.properties.StringProperty

object PokemonFlagProperty : CustomPokemonPropertyType<StringProperty> {

    private const val KEY = "tag"

    override val keys = setOf(KEY)
    override val needsKey = true

    override fun fromString(value: String?) = if (value == null) null else StringProperty(KEY, value, { _, _ -> }, { pokemon, underlyingValue -> pokemon.hasTags(underlyingValue) })

}