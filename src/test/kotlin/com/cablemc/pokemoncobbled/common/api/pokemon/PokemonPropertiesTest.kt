package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.pokemon.Gender
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PokemonPropertiesTest {
    @Nested
    inner class ParseTest {
        @Test
        fun `should parse appropriately for typical case`() {
            val str = "bulbasaur lvl=4 male"
            val spec = PokemonProperties.parse(str)
            assertEquals(Gender.MALE, spec.gender)
            assertEquals(4, spec.level)
            assertEquals("bulbasaur", spec.species)
        }
    }
}