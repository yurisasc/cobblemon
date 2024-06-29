/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.util.isServerSide
import com.cobblemon.mod.common.util.math.geometry.toRadians
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.Level
import kotlin.math.cos

class PokeBallItem(
    val pokeBall : PokeBall
) : CobblemonItem(Settings()) {

    override fun use(world: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        if (world.isServerSide()) {
            throwPokeBall(world, player as ServerPlayer)
        }
        if (!player.abilities.creativeMode) {
            itemStack.shrink(1)
        }
        return InteractionResultHolder.success(itemStack, world.isClient)
    }

    private fun throwPokeBall(world: Level, player: ServerPlayer) {
        val pokeBallEntity = EmptyPokeBallEntity(pokeBall, player.level(), player).apply {
            val overhandFactor: Float = if (player.pitch < 0) {
                5f * cos(player.pitch.toRadians())
            } else {
                5f
            }
            setVelocity(player, player.pitch - overhandFactor, player.yaw, 0.0f, pokeBall.throwPower, 1.0f)
            setPosition(pos.add(velocity.normalize().multiply(1.0)))
            owner = player
        }
        world.spawnEntity(pokeBallEntity)
    }

    /*
    override fun isFireproof(): Boolean {
        return pokeBall.name == PokeBalls.MASTER_BALL.name || super.isFireproof()
    }

     */

}