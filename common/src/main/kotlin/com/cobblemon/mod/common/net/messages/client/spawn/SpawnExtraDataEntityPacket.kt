/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.spawn

import com.cobblemon.mod.common.api.net.NetworkPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket

abstract class SpawnExtraDataEntityPacket<E: Entity>(private val vanillaSpawnPacket: EntitySpawnS2CPacket) : NetworkPacket<SpawnExtraDataEntityPacket<E>>  {
    override fun encode(buffer: PacketByteBuf) {
        this.encodeEntityData(buffer)
        this.vanillaSpawnPacket.write(buffer)
    }

    abstract fun encodeEntityData(buffer: PacketByteBuf)

    abstract fun applyData(entity: E)

    abstract fun checkType(entity: Entity): Boolean

    fun spawnAndApply(client: MinecraftClient) {
        client.execute {
            val player = client.player ?: return@execute
            player.networkHandler.onEntitySpawn(this.vanillaSpawnPacket)
            val entity = player.world.getEntityById(this.vanillaSpawnPacket.id) ?: return@execute
            if (this.checkType(entity)) {
                this.applyData(entity as E)
            }
        }
    }

    companion object {
        fun decodeVanillaPacket(buffer: PacketByteBuf) = EntitySpawnS2CPacket(buffer)
    }
}