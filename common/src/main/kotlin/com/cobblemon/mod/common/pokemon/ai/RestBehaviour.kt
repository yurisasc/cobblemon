/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.ai

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.ai.SleepDepth
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.biome.Biome

/**
 * Behavioural properties relating to a Pokémon sleeping. This can be wild sleeping or sleeping on the player or both.
 *
 * @author Hiroku
 * @since July 16th, 2022
 */
data class RestBehaviour(
    val canSleep: Boolean = false,
    val times: TimeRange = TimeRange.timeRanges["night"]!!,
    val sleepChance: Float = 1 / 600F,
    val blocks: MutableList<RegistryLikeCondition<Block>> = mutableListOf(),
    val biomes: MutableList<RegistryLikeCondition<Biome>> = mutableListOf(),
    val light: IntRange = IntRange(0, 15),
    val depth: SleepDepth = SleepDepth.normal,
    val willSleepOnBed: Boolean = false
) {

    companion object {

        @JvmField
        val CODEC: Codec<RestBehaviour> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.BOOL.optionalFieldOf("canSleep", false).forGetter(RestBehaviour::canSleep),
                TimeRange.CODEC.optionalFieldOf("times", TimeRange(12542..23459)).forGetter(RestBehaviour::times),
                Codec.FLOAT.optionalFieldOf("sleepChance", 1 / 600F).forGetter(RestBehaviour::sleepChance),
                Codec.list(RegistryLikeCondition.createCodec { Registries.BLOCK }).optionalFieldOf("blocks", mutableListOf()).forGetter(RestBehaviour::blocks),
                Codec.list(RegistryLikeCondition.createCodec { Cobblemon.implementation.getRegistry(RegistryKeys.BIOME) }).optionalFieldOf("biomes", mutableListOf()).forGetter(RestBehaviour::biomes),
                Codec.pair(Codec.intRange(0, 15), Codec.intRange(0, 15)).optionalFieldOf("light", Pair(0, 15)).xmap({ IntRange(it.first, it.second) }, { Pair(it.first, it.last) }).forGetter(RestBehaviour::light),
                SleepDepth.CODEC.optionalFieldOf("depth", SleepDepth.normal).forGetter(RestBehaviour::depth),
                Codec.BOOL.optionalFieldOf("willSleepOnBed", false).forGetter(RestBehaviour::willSleepOnBed)
            ).apply(builder, ::RestBehaviour)
        }

    }

}