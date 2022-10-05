/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.data

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategories
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.cablemc.pokemoncobbled.common.battles.MoveTarget
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.network.PacketByteBuf

class MovesRegistrySyncPacket : DataRegistrySyncPacket<MoveTemplate>(Moves.all()) {
    override fun encodeEntry(buffer: PacketByteBuf, entry: MoveTemplate) {
        buffer.writeString(entry.name)
        buffer.writeString(entry.elementalType.name)
        buffer.writeString(entry.damageCategory.name)
        buffer.writeDouble(entry.power)
        buffer.writeEnumConstant(entry.target)
        buffer.writeDouble(entry.accuracy)
        buffer.writeInt(entry.pp)
        buffer.writeInt(entry.priority)
        buffer.writeDouble(entry.critRatio)
        buffer.writeNullable(entry.effectChance) { pb, value -> pb.writeDouble(value) }
        buffer.writeNullable(entry.effectStatus) { pb, value -> pb.writeString(value) }
    }

    override fun decodeEntry(buffer: PacketByteBuf): MoveTemplate? {
        val name = buffer.readString()
        val type = ElementalTypes.getOrException(buffer.readString())
        val damageCategory = DamageCategories.getOrException(buffer.readString())
        val power = buffer.readDouble()
        val target = buffer.readEnumConstant(MoveTarget::class.java)
        val accuracy = buffer.readDouble()
        val pp = buffer.readInt()
        val priority = buffer.readInt()
        val critRatio = buffer.readDouble()
        val effectChance = buffer.readNullable { pb -> pb.readDouble() } ?: .0
        val effectStatus = buffer.readNullable { pb -> pb.readString() } ?: ""
        return MoveTemplate(name, type, damageCategory, power, target, accuracy, pp, priority, critRatio, effectChance, effectStatus)
    }

    override fun synchronizeDecoded(entries: Collection<MoveTemplate>) {
        Moves.reload(entries.associateBy { cobbledResource(it.name) })
    }

}