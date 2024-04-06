/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.pokemon.ai.tasks

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.DataKeys
import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.ai.brain.MemoryModuleState
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.task.MultiTickTask
import net.minecraft.predicate.block.BlockStatePredicate
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.GameRules

/**
 * The baa's eat the graa's
 *
 * @author Hiroku
 * @since April 6th, 2024
 */
class EatGrassTask : MultiTickTask<PokemonEntity>(
    ImmutableMap.of(
        MemoryModuleType.ANGRY_AT, MemoryModuleState.VALUE_ABSENT,
        MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT,
        MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
        CobblemonMemories.POKEMON_BATTLE, MemoryModuleState.VALUE_ABSENT
    )
) {
    val grassPredicate = BlockStatePredicate.forBlock(Blocks.GRASS)
    var timer = -1

    override fun shouldRun(world: ServerWorld, entity: PokemonEntity): Boolean {
        if (entity.random.nextInt(500) != 0) {
            return false
        } else {
            entity.pokemon.getFeature<FlagSpeciesFeature>(DataKeys.HAS_BEEN_SHEARED)?.enabled?.takeIf { it } ?: run {
                return false
            }
            val blockPos = entity.blockPos
            if (grassPredicate.test(world.getBlockState(blockPos)) ) {
                return true
            } else if (world.getBlockState(blockPos.down()).isOf(Blocks.GRASS_BLOCK)) {
                return true
            }
            return false
        }
    }

    override fun run(world: ServerWorld, entity: PokemonEntity, time: Long) {
        timer = 40
        world.sendEntityStatus(entity, 10.toByte())
    }

    override fun shouldKeepRunning(world: ServerWorld, entity: PokemonEntity, time: Long): Boolean {
        timer--
        if (timer < 0) {
            val blockPos = entity.blockPos
            if (grassPredicate.test(world.getBlockState(blockPos)) ) {
                if (world.gameRules.getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    world.breakBlock(blockPos, false)
                }
                entity.onEatingGrass()
            } else if (world.getBlockState(blockPos.down()).isOf(Blocks.GRASS_BLOCK)) {
                val blockPos2 = blockPos.down()
                if (world.getBlockState(blockPos2).isOf(Blocks.GRASS_BLOCK)) {
                    if (world.gameRules.getBoolean(GameRules.DO_MOB_GRIEFING)) {
                        world.syncWorldEvent(2001, blockPos2, Block.getRawIdFromState(Blocks.GRASS_BLOCK.defaultState))
                        world.setBlockState(blockPos2, Blocks.DIRT.defaultState, 2)
                    }

                    entity.onEatingGrass()
                }
            }
            return false
        }
        return true
    }
}