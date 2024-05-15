/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.sensors

import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.LivingTargetCache
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.sensor.Sensor
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.server.world.ServerWorld

/**
 * Senses when the Pokémon is near an adult of the same species.
 *
 * @author broccoli
 * @since May 15th, 2024
 */
class PokemonAdultSensor : Sensor<PokemonEntity>(100) {
    override fun getOutputMemoryModules() = setOf(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_MOBS)

    override fun sense(world: ServerWorld, entity: PokemonEntity) {
        entity.brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent { visibleMobs ->
            setNearestPokemonAdult(entity, visibleMobs)
        }
    }

    private fun setNearestPokemonAdult(entity: PokemonEntity, visibleMobs: LivingTargetCache) {
        val nearestAdult = visibleMobs.findFirst { mob ->
            targetIsValidForChild(entity, mob)
        }.map { mob -> mob as PassiveEntity }

        entity.brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT, nearestAdult)
    }

    private fun targetIsValidForChild(child: PokemonEntity, entity: LivingEntity): Boolean {
        val potentialParent = entity as? PokemonEntity ?: return false
        //todo(broccoli): better way to compare species?
        val childSpecies = child.pokemon.species.resourceIdentifier

        var preEvolution = potentialParent.pokemon.preEvolution

        while (preEvolution != null) {
            if (childSpecies == preEvolution.species.resourceIdentifier) {
                LOGGER.info("found parent {} for child {}", entity.pokemon.species.name, child.pokemon.species.name)
                return true
            }

            preEvolution = preEvolution.species.preEvolution
        }

        return false
    }
}