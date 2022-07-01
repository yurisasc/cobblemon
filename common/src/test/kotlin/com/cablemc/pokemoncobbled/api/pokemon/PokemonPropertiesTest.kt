package com.cablemc.pokemoncobbled.api.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.util.adapters.pokemonPropertiesShortAdapter
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PokemonPropertiesTest {
    @Nested
    inner class ParseTest {
        @Test
        fun `should parse appropriately for typical case`() {
            val str = "bulbasaur lvl=4 male"
            val spec = PokemonProperties.parse(str)
//            assertEquals(Gender.MALE, spec.gender)
            assertEquals(4, spec.level)
            assertEquals("bulbasaur", spec.species)
        }

        @Test
        fun `JSON short serialization`() {
            val gson = GsonBuilder().registerTypeAdapter(PokemonProperties::class.java, pokemonPropertiesShortAdapter).create()
            val json = JsonPrimitive("ivysaur lvl=16")
            val properties = gson.fromJson<PokemonProperties>(json)
            assertEquals(16, properties.level)
            assertEquals("ivysaur", properties.species)
        }

    }
}