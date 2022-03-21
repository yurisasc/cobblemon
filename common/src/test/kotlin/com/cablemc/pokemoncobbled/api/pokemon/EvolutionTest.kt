package com.cablemc.pokemoncobbled.api.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.LevelEvolution
import com.cablemc.pokemoncobbled.common.pokemon.evolution.TradeEvolution
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Evolution test shenanigans.
 *
 * @author Licious
 * @since March 20th, 2022
 */
internal class EvolutionTest {

    @Test
    fun `test serialize`() {
        val results = PokemonSpecies.BULBASAUR.evolutions
        assertTrue(results.isNotEmpty())
        val evolution = results.first()
        assertEquals(PokemonSpecies.IVYSAUR.name, evolution.result.species)
    }

    @Test
    fun `test level`() {
        val evolution = LevelEvolution(
            id = "test",
            result = PokemonProperties.parse("ivysaur"),
            levels = 16..16,
            optional = false,
            consumeHeldItem = false,
            requirements = emptyList()
        )
        val pokemon = Pokemon().apply {
            species = PokemonSpecies.BULBASAUR
            level = 15
        }
        evolution.attemptEvolution(pokemon)
        assertEquals(PokemonSpecies.BULBASAUR.name, pokemon.species.name)
        pokemon.level++
        evolution.attemptEvolution(pokemon)
        assertEquals(PokemonSpecies.IVYSAUR.name, pokemon.species.name)
    }

    @Test
    fun `test trade`() {
        val evolution = TradeEvolution(
            id = "test",
            result = PokemonProperties.parse(PokemonSpecies.IVYSAUR.name),
            requiredContext = PokemonProperties.parse(PokemonSpecies.CHARMANDER.name),
            optional = false,
            consumeHeldItem = false,
            requirements = emptyList()
        )
        val pokemon = Pokemon().apply { species = PokemonSpecies.BULBASAUR }
        val with = Pokemon().apply { species = PokemonSpecies.CHARMELEON }
        evolution.attemptEvolution(pokemon, with)
        assertEquals(PokemonSpecies.BULBASAUR.name, pokemon.species.name)
        with.species = PokemonSpecies.CHARMANDER
        evolution.attemptEvolution(pokemon, with)
        assertEquals(PokemonSpecies.IVYSAUR.name, pokemon.species.name)
    }

    @Test
    fun `test item interaction`() {
        // ToDo Make this actually testable since it needs to boot god knows what on MCs end
        /*
        val evolution = ItemInteractionEvolution(
            id = "test",
            to = PokemonProperties.parse("ivysaur"),
            requiredContext = ItemStack(Items.VINE),
            optional = false,
            consumeHeldItem = false,
            requirements = emptyList()
        )
        val pokemon = Pokemon().apply { species = PokemonSpecies.BULBASAUR }
        evolution.attemptEvolution(pokemon, ItemStack(Items.POTATO))
        assertEquals(PokemonSpecies.BULBASAUR.name, pokemon.species.name)
        evolution.attemptEvolution(pokemon, ItemStack(Items.VINE))
        assertEquals(PokemonSpecies.IVYSAUR.name, pokemon.species.name)
         */
    }

}