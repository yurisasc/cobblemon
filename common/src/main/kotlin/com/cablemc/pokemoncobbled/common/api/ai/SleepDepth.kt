package com.cablemc.pokemoncobbled.common.api.ai

import com.cablemc.pokemoncobbled.common.api.serialization.StringIdentifiedObjectAdapter
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.util.math.Box

/**
 * How deeply a Pokémon sleeps. This works as a boolean function that takes the current situation and returns true
 * if the Pokémon is able to sleep at this depth.
 *
 * A depth should be registered by name in [SleepDepth.depths].
 *
 * @author Hiroku
 * @since July 17th, 2022
 */
fun interface SleepDepth {
    companion object {
        val comatose = SleepDepth { true }
        val normal = SleepDepth { pokemonEntity ->
            val nearbyPlayers = pokemonEntity.world.getPlayers(TargetPredicate.DEFAULT, pokemonEntity, Box.of(pokemonEntity.pos, 16.0, 16.0, 16.0))
            return@SleepDepth nearbyPlayers.none { !it.isSneaking }
        }
        val depths = mutableMapOf(
            "comatose" to comatose,
            "normal" to normal
        )
        val adapter = StringIdentifiedObjectAdapter { depths[it] ?: throw IllegalArgumentException("Unknown sleep depth: $it") }
    }

    fun canSleep(pokemonEntity: PokemonEntity): Boolean
}