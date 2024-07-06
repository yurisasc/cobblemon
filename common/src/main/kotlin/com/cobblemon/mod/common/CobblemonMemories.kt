/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import net.minecraft.entity.LivingEntity
import java.util.Optional
import java.util.UUID
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.util.Uuids
import net.minecraft.util.math.BlockPos

object CobblemonMemories {
    val memories = mutableMapOf<String, MemoryModuleType<*>>()

    val POKEMON_FLYING = register("pokemon_flying", PrimitiveCodec.BOOL)
    val POKEMON_DROWSY = register("pokemon_drowsy", PrimitiveCodec.BOOL)
    val POKEMON_SLEEPING = register("pokemon_sleeping", PrimitiveCodec.BOOL)
    val POKEMON_BATTLE = register<UUID>("pokemon_battle") // No codec because it shouldn't survive relogs
    val REST_PATH_COOLDOWN = register("rest_path_cooldown", PrimitiveCodec.BOOL)
    val TARGETED_BATTLE_POKEMON = register<UUID>("targeted_battle_pokemon")
    val NEAREST_VISIBLE_ATTACKER = register<LivingEntity>("nearest_visible_attacker")
    val NEARBY_GROWABLE_CROPS = register<BlockPos>("nearby_growable_crops")

    fun <U> register(id: String, codec: Codec<U>): MemoryModuleType<U> {
        val memoryModule = MemoryModuleType(Optional.of(codec))
        memories[id] = memoryModule
        return memoryModule
    }

    fun <U> register(id: String): MemoryModuleType<U> {
        val memoryModule = MemoryModuleType<U>(Optional.empty())
        memories[id] = memoryModule
        return memoryModule
    }
}
