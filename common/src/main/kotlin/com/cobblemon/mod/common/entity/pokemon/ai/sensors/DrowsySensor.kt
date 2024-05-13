/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.sensors

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.CobblemonSensors
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.sensor.SensorType
import net.minecraft.server.world.ServerWorld
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor

/**
 * Senses when the Pokémon is in a situation that would make it ready to sleep.
 *
 * @author Hiroku
 * @since March 23rd, 2024
 */
class DrowsySensor : ExtendedSensor<PokemonEntity>() {
    override fun memoriesUsed(): List<MemoryModuleType<*>> {
        return listOf(
            CobblemonMemories.POKEMON_DROWSY
        )
    }

    override fun type(): SensorType<out ExtendedSensor<*>> {
        return CobblemonSensors.POKEMON_DROWSY
    }

    override fun sense(world: ServerWorld, entity: PokemonEntity) {
        val rest = entity.behaviour.resting
        val isDrowsy = entity.brain.getOptionalRegisteredMemory(CobblemonMemories.POKEMON_DROWSY).orElse(false)
        val shouldBeDrowsy = rest.canSleep && world.timeOfDay.toInt() in rest.times
        if (!isDrowsy && shouldBeDrowsy) {
            entity.brain.remember(CobblemonMemories.POKEMON_DROWSY, true)
        } else if (isDrowsy && !shouldBeDrowsy) {
            entity.brain.forget(CobblemonMemories.POKEMON_DROWSY)
        }
    }
}