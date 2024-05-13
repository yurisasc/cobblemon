/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.ai

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.toDF
import com.mojang.datafixers.util.Pair
import net.minecraft.entity.ai.brain.MemoryModuleState
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour
import net.tslat.smartbrainlib.util.BrainUtils

//todo: make sure it's the same behaviour
class GetAngryAtAttackerTask : ExtendedBehaviour<PokemonEntity>() {
    init {
        whenStarting { entity ->
            val hurtByEntity = requireNotNull(
                BrainUtils.getMemory(entity, MemoryModuleType.HURT_BY_ENTITY)
            )

            val angryAt = BrainUtils.getMemory(entity, MemoryModuleType.ANGRY_AT)
            if (angryAt != null && angryAt == hurtByEntity.uuid) {
                return@whenStarting
            }

            entity.brain.remember(MemoryModuleType.ANGRY_AT, hurtByEntity.uuid)
        }
    }

    override fun getMemoryRequirements(): List<Pair<MemoryModuleType<*>, MemoryModuleState>> {
        //todo: not sure what state these should be
        return listOf(
            MemoryModuleType.HURT_BY_ENTITY toDF MemoryModuleState.REGISTERED,
            MemoryModuleType.ANGRY_AT toDF MemoryModuleState.REGISTERED,
        )
    }
}
