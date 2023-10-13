/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.data

import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.moves.DamageCategory
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.battles.MoveTarget
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf

class MovesRegistrySyncPacket(moves: List<MoveTemplate>) : DataRegistrySyncPacket<MoveTemplate, MovesRegistrySyncPacket>(moves) {

    override val id = ID

    override fun encodeEntry(buffer: PacketByteBuf, entry: MoveTemplate) {
        buffer.writeString(entry.name)
        buffer.writeInt(entry.num)
        buffer.writeRegistryValue(CobblemonRegistries.ELEMENTAL_TYPE, entry.elementalType)
        buffer.writeEnumConstant(entry.damageCategory)
        buffer.writeDouble(entry.power)
        buffer.writeEnumConstant(entry.target)
        buffer.writeDouble(entry.accuracy)
        buffer.writeInt(entry.pp)
        buffer.writeInt(entry.priority)
        buffer.writeDouble(entry.critRatio)
        buffer.writeVarInt(entry.effectChances.size)
        entry.effectChances.forEach { chance -> buffer.writeDouble(chance) }
    }

    override fun decodeEntry(buffer: PacketByteBuf): MoveTemplate {
        val name = buffer.readString()
        val num = buffer.readInt()
        val type = buffer.readRegistryValue(CobblemonRegistries.ELEMENTAL_TYPE)!!
        val damageCategory = buffer.readEnumConstant(DamageCategory::class.java)
        val power = buffer.readDouble()
        val target = buffer.readEnumConstant(MoveTarget::class.java)
        val accuracy = buffer.readDouble()
        val pp = buffer.readInt()
        val priority = buffer.readInt()
        val critRatio = buffer.readDouble()
        val effectChances = arrayListOf<Double>()
        repeat(buffer.readVarInt()) {
            effectChances += buffer.readDouble()
        }
        return MoveTemplate(name, num, type, damageCategory, power, target, accuracy, pp, priority, critRatio, effectChances.toTypedArray())
    }

    override fun synchronizeDecoded(entries: Collection<MoveTemplate>) {
        Moves.receiveSyncPacket(entries)
    }

    companion object {
        val ID = cobblemonResource("moves_sync")
        fun decode(buffer: PacketByteBuf): MovesRegistrySyncPacket = MovesRegistrySyncPacket(emptyList()).apply { decodeBuffer(buffer) }
    }
}