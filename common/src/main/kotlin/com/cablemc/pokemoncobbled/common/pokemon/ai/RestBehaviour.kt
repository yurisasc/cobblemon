package com.cablemc.pokemoncobbled.common.pokemon.ai

import com.cablemc.pokemoncobbled.common.api.ai.SleepDepth
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.TimeRange
import net.minecraft.block.Block
import net.minecraft.world.biome.Biome

/**
 * Behavioural properties relating to a Pokémon sleeping. This can be wild sleeping or sleeping on the player or both.
 *
 * @author Hiroku
 * @since July 16th, 2022
 */
class RestBehaviour {
    val canSleep = false
    val times = TimeRange.ranges["night"]!!
    val sleepChance = 1 / 600F
    val blocks = mutableListOf<RegistryLikeCondition<Block>>()
    val biomes = mutableListOf<RegistryLikeCondition<Biome>>()
    val light = IntRange(0, 15)
    val depth = SleepDepth.normal

    val willSleepOnBed = false
}