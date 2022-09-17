/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.item.CobbledItemGroups.POKE_BALL_GROUP
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.isServerSide
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import kotlin.math.cos

class PokeBallItem(
    val pokeBall : PokeBall
) : CobbledItem(Settings().group(POKE_BALL_GROUP)) {

    override fun use(world: World, player: PlayerEntity, usedHand: Hand): TypedActionResult<ItemStack> {
        val itemStack = player.getStackInHand(usedHand)
        if (world.isServerSide()) {
            throwPokeBall(world, player as ServerPlayerEntity)
        }
        if (!player.abilities.creativeMode) {
            itemStack.decrement(1)
        }
        return TypedActionResult.success(itemStack, world.isClient)
    }

    private fun throwPokeBall(world: World, player: ServerPlayerEntity) {
        val pokeBallEntity = EmptyPokeBallEntity(pokeBall, player.world).apply {
            setPos(player.x, player.y + player.standingEyeHeight - 0.2, player.z)
            setVelocity(player, player.pitch - 10 * cos(player.pitch.toRadians()), player.yaw, 0.0f, 1.25f, 1.0f)
            owner = player
        }
        world.spawnEntity(pokeBallEntity)
    }
}