/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.CobblemonActivities
import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer

object GoToSleepTask {
    fun create(): SingleTickTask<PokemonEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryAbsent(MemoryModuleType.ANGRY_AT),
                it.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET),
                it.queryMemoryAbsent(MemoryModuleType.WALK_TARGET),
                it.queryMemoryAbsent(CobblemonMemories.POKEMON_BATTLE),
                it.queryMemoryOptional(CobblemonMemories.POKEMON_DROWSY)
            ).apply(it) { _, _, _, _, pokemonDrowsy ->
                TaskRunnable { world, entity, _ ->
                    val hasSleepStatus = entity.pokemon.status?.status === Statuses.SLEEP
                    if (entity.behaviour.resting.canSleep && ((it.getOptionalValue(pokemonDrowsy).orElse(false) && entity.canSleepAt(entity.blockPos.down())) || hasSleepStatus) && entity.pokemon.storeCoordinates.get()?.store !is PartyStore) {
                        if (!hasSleepStatus) {
                            entity.pokemon.status = PersistentStatusContainer(Statuses.SLEEP)
                        }
                        entity.brain.resetPossibleActivities(listOf(CobblemonActivities.POKEMON_SLEEPING_ACTIVITY))
                        entity.brain.remember(CobblemonMemories.POKEMON_SLEEPING, true)
                        return@TaskRunnable true
                    } else {
                        return@TaskRunnable false
                    }
                }
            }
        }
    }
}