package com.cablemc.pokemoncobbled.common.pokemon.properties.tags

import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonPropertyType
import com.cablemc.pokemoncobbled.common.util.asIdentifierDefaultingNamespace
import net.minecraft.util.InvalidIdentifierException

object PokemonFlagProperty : CustomPokemonPropertyType<PokemonTagQueryProperty> {

    private const val KEY = "tag"

    override val keys = setOf(KEY)
    override val needsKey = true

    override fun fromString(value: String?) = try {
        if (value == null) null else PokemonTagQueryProperty(KEY, value.asIdentifierDefaultingNamespace())
    } catch (e: InvalidIdentifierException) {
        null
    }

}