/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.spawn

import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.entity.Entity
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.util.Identifier

class SpawnPokeballPacket(
    val pokeBall: PokeBall,
    vanillaSpawnPacket: EntitySpawnS2CPacket
) : SpawnExtraDataEntityPacket<EmptyPokeBallEntity>(vanillaSpawnPacket) {

    override val id: Identifier = ID

    override fun encodeEntityData(buffer: PacketByteBuf) {
        buffer.writeIdentifier(this.pokeBall.name)
    }

    override fun applyData(entity: EmptyPokeBallEntity) {
        entity.pokeBall = this.pokeBall
    }

    override fun checkType(entity: Entity): Boolean = entity is EmptyPokeBallEntity

    companion object {
        val ID = cobblemonResource("spawn_empty_pokeball_entity")
        fun decode(buffer: PacketByteBuf): SpawnPokeballPacket {
            val pokeBall = PokeBalls.getPokeBall(buffer.readIdentifier())!!
            val vanillaPacket = decodeVanillaPacket(buffer)
            return SpawnPokeballPacket(pokeBall, vanillaPacket)
        }
    }

}