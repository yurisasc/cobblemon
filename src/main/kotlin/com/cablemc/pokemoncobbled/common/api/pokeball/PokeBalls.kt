package com.cablemc.pokemoncobbled.common.api.pokeball

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokeball.catch.modifiers.MultiplierModifier
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import net.minecraft.resources.ResourceLocation

/**
 * Main API point for Pokeballs
 *
 * Get or register Pokeballs
 */
object PokeBalls {
    private val allPokeBalls = mutableListOf<PokeBall>()

    val POKE_BALL = registerPokeBall(PokeBall(ResourceLocation(PokemonCobbled.MODID, "poke_ball")))
    val GREAT_BALL = registerPokeBall(PokeBall(ResourceLocation(PokemonCobbled.MODID, "great_ball"), listOf(MultiplierModifier(1.5f))))
    val ULTRA_BALL = registerPokeBall(PokeBall(ResourceLocation(PokemonCobbled.MODID, "ultra_ball"), listOf(MultiplierModifier(2f))))

    /**
     * Registers a new pokeball type.
     * @return the pokeball type.
     */
    fun registerPokeBall(pokeBall: PokeBall) : PokeBall {
        allPokeBalls.add(pokeBall)
        return pokeBall
    }

    /**
     * Gets a Pokeball from registry name.
     * @return the pokeball object if found otherwise null.
     */
    fun getPokeBall(name : ResourceLocation) : PokeBall? {
        return allPokeBalls.find { pokeball -> pokeball.name == name }
    }
}