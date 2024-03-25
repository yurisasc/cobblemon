/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.storage.party.PartyStore
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer

object WakeUpTask {
    fun create(): SingleTickTask<PokemonEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryAbsent(CobblemonMemories.POKEMON_BATTLE),
                it.queryMemoryAbsent(CobblemonMemories.POKEMON_DROWSY)
            ).apply(it) { _, _ ->
                TaskRunnable { world, entity, _ ->
                    if (entity.pokemon.status?.status == Statuses.SLEEP && entity.pokemon.storeCoordinates.get()?.store !is PartyStore) {
                        entity.pokemon.status = null
                        entity.brain.resetPossibleActivities()
                        return@TaskRunnable true
                    } else {
                        return@TaskRunnable false
                    }
                }
            }
        }
    }
}