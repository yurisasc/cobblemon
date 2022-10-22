/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.ai

import com.cablemc.pokemod.common.api.ai.SleepDepth
import com.cablemc.pokemod.common.api.conditional.RegistryLikeCondition
import com.cablemc.pokemod.common.api.spawning.condition.TimeRange
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