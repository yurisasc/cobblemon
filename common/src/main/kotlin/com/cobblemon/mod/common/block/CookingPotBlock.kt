/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block

import com.cobblemon.mod.common.block.entity.CookingPotBlockEntity
import net.minecraft.block.AbstractFurnaceBlock
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.FurnaceBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World


class CookingPotBlock constructor(settings: Settings?) : AbstractFurnaceBlock(settings) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return CookingPotBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(world, type, BlockEntityType.FURNACE)
    }

    override fun openScreen(world: World, pos: BlockPos, player: PlayerEntity) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is CookingPotBlockEntity) {
            player.openHandledScreen(blockEntity as NamedScreenHandlerFactory?)
            player.incrementStat(Stats.INTERACT_WITH_FURNACE)
        }
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if (state.get(LIT) as Boolean) {
            val d = pos.x.toDouble() + 0.5
            val e = pos.y.toDouble()
            val f = pos.z.toDouble() + 0.5
            if (random.nextDouble() < 0.1) {
                world.playSound(
                    d,
                    e,
                    f,
                    SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                    SoundCategory.BLOCKS,
                    1.0f,
                    1.0f,
                    false
                )
            }

            val direction = state.get(FACING) as Direction
            val axis = direction.axis
            val g = 0.52
            val h = random.nextDouble() * 0.6 - 0.3
            val i = if (axis === Direction.Axis.X) direction.offsetX.toDouble() * 0.52 else h
            val j = random.nextDouble() * 6.0 / 16.0
            val k = if (axis === Direction.Axis.Z) direction.offsetZ.toDouble() * 0.52 else h
            world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0)
            world.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0, 0.0, 0.0)
        }
    }
}
