/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.brain.EntityLookTarget
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.ai.brain.task.TaskRunnable
import net.minecraft.entity.ai.brain.task.TaskTriggerer

/**
 * Manages the look target of a Pokémon in battle.
 *
 * @author Hiroku
 * @since April 8th, 2024
 */
object LookAtTargetedBattlePokemonTask {
    fun create(): SingleTickTask<PokemonEntity> {
        return TaskTriggerer.task {
            it.group(
                it.queryMemoryValue(CobblemonMemories.POKEMON_BATTLE),
                it.queryMemoryOptional(CobblemonMemories.TARGETED_BATTLE_POKEMON),
                it.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
            ).apply(it) { _, targetedBattlePokemon, lookTarget ->
                TaskRunnable { world, entity, _ ->
                    val targeted = it.getOptionalValue(targetedBattlePokemon).orElse(null)
                    val targetedEntity = targeted?.let { world.getEntity(it) as? PokemonEntity }
                    val look = it.getOptionalValue(lookTarget).orElse(null) as? EntityLookTarget
                    if (targeted != null && targetedEntity == null) {
                        entity.brain.forget(CobblemonMemories.TARGETED_BATTLE_POKEMON)
                        return@TaskRunnable false
                    } else if (targetedEntity != null && look?.entity != targetedEntity) {
                        entity.brain.remember(MemoryModuleType.LOOK_TARGET, EntityLookTarget(targetedEntity, true))
                        return@TaskRunnable true
                    } else if (targetedEntity == null) {
                        val battle = entity.battle ?: return@TaskRunnable false
                        val nearestOpposingPokemon = battle.sides
                            .find { entity in it.actors.flatMap { it.pokemonList.map { it.entity } } }
                            ?.getOppositeSide()?.actors
                            ?.flatMap { it.pokemonList.mapNotNull { it.entity } }
                            ?.minByOrNull { it.distanceTo(entity) }
                        if (nearestOpposingPokemon != null) {
                            entity.brain.remember(MemoryModuleType.LOOK_TARGET, EntityLookTarget(nearestOpposingPokemon, true))
                            return@TaskRunnable true
                        }
                    }
                    return@TaskRunnable false
                }
            }
        }
    }
}