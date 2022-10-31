/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.spawning.spawner

import com.cablemc.pokemod.common.Pokemod.config
import com.cablemc.pokemod.common.api.spawning.SpawnCause
import com.cablemc.pokemod.common.api.spawning.SpawnerManager
import com.cablemc.pokemod.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemod.common.util.getPlayer
import com.cablemc.pokemod.common.util.squeezeWithinBounds
import java.util.UUID
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper.PI
import net.minecraft.util.math.MathHelper.ceil
import net.minecraft.util.math.MathHelper.nextBetween
import net.minecraft.util.math.random.Random

/**
 * A spawner that works around a single player. It will do basic tracking of a player's speed
 * and project that into the future and spawn in that direction.
 *
 * @author Hiroku
 * @since February 14th, 2022
 */
class PlayerSpawner(player: ServerPlayerEntity, spawns: SpawnPool, manager: SpawnerManager) : AreaSpawner(player.name.string, spawns, manager) {
    val uuid: UUID = player.uuid

    override fun getCauseEntity() = uuid.getPlayer()
    override fun getArea(cause: SpawnCause): SpawningArea? {
        val player = uuid.getPlayer() ?: return null
        val sliceDiameter = config.worldSliceDiameter
        val sliceHeight = config.worldSliceHeight

        val rand = Random.create()

        val center = player.pos

        val r = nextBetween(rand, config.minimumSliceDistanceFromPlayer, config.maximumSliceDistanceFromPlayer)
        val thetatemp = atan(player.velocity.z / player.velocity.x) + nextBetween(rand, -PI/2, PI/2 )
        val theta = if (player.velocity.horizontalLength() < 0.1) {
            rand.nextDouble() * 2 * PI
        } else if (player.velocity.x < 0) {
            PI - thetatemp
        } else {
            thetatemp
        }
        val x = center.x + r * cos(theta)
        val z = center.z + r * sin(theta)

        val lowestX = ceil(x - sliceDiameter / 2F)
        val lowestY = ceil(center.y - sliceHeight / 2F)
        val lowestZ = ceil(z - sliceDiameter / 2F)
        val lowestPos = BlockPos(lowestX, lowestY, lowestZ)

        val min = player.world.squeezeWithinBounds(lowestPos)
        val max = player.world.squeezeWithinBounds(lowestPos.add(sliceDiameter, sliceHeight, sliceDiameter))

        return if (player.world.canSetBlock(min) && player.world.canSetBlock(max)) {
            SpawningArea(
                cause = cause,
                world = player.world,
                baseX = min.x,
                baseY = min.y,
                baseZ = min.z,
                length = sliceDiameter,
                height = sliceHeight,
                width = sliceDiameter
            )
        } else {
            null
        }
    }
}