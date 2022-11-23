/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.ai

import com.cobblemon.mod.common.api.serialization.StringIdentifiedObjectAdapter
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
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
            val nearbyPlayers = pokemonEntity.world.getPlayers(TargetPredicate.createNonAttackable(), pokemonEntity, Box.of(pokemonEntity.pos, 8.0, 8.0, 8.0))
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