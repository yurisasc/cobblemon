/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.fishing

import com.cobblemon.mod.common.api.fishing.FishingBait
import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.net.messages.client.data.BerryRegistrySyncPacket
import com.cobblemon.mod.common.net.messages.client.data.DataRegistrySyncPacket
import com.cobblemon.mod.common.registry.ItemTagCondition
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.tag.TagKey
import java.lang.StringBuilder


class FishingBaitRegistrySyncPacket(fishingBaits: List<FishingBait>) : DataRegistrySyncPacket<FishingBait, FishingBaitRegistrySyncPacket>(fishingBaits) {
    companion object {
        val ID = cobblemonResource("fishing_baits")
        fun decode(buffer: PacketByteBuf) = FishingBaitRegistrySyncPacket(emptyList()).apply { decodeBuffer(buffer) }
    }


    override val id = ID
    override fun encodeEntry(buffer: PacketByteBuf, entry: FishingBait) {
        buffer.writeNullable(entry.item) {pb, type -> pb.writeIdentifier(entry.item)}
        buffer.writeNullable(entry.tag) { pb, type -> pb.writeString(FishingBaits.gson.toJson("#" + entry.tag?.tag?.id.toString()) ) }
        buffer.writeNullable(entry.effect) { pb, type -> pb.writeString(entry.effect) }
        buffer.writeNullable(entry.subcategory) { pb, type -> pb.writeString(entry.subcategory) }
        buffer.writeNullable(entry.chance) { pb, type -> pb.writeDouble(entry.chance ?: 0.0) }
        buffer.writeNullable(entry.value) { pb, type -> pb.writeDouble(entry.value ?: 0.0) }
        buffer.writeNullable(entry.note) { pb, type -> pb.writeString(entry.note) }

    }

    override fun decodeEntry(buffer: PacketByteBuf): FishingBait {
        return FishingBait (
                item = buffer.readNullable { pb -> pb.readIdentifier() },
                tag = buffer.readNullable { pb -> FishingBaits.gson.fromJson(buffer.readString(), ItemTagCondition::class.java) },
                effect = buffer.readNullable { pb -> FishingBaits.gson.fromJson(buffer.readString(), String::class.java) },
                subcategory = buffer.readNullable { pb -> FishingBaits.gson.fromJson(buffer.readString(), String::class.java) },
                chance = buffer.readNullable { pb -> FishingBaits.gson.fromJson(buffer.readString(), Double::class.java) },
                value = buffer.readNullable { pb -> FishingBaits.gson.fromJson(buffer.readString(), Double::class.java) },
                note = buffer.readNullable { pb -> FishingBaits.gson.fromJson(buffer.readString(), String::class.java) }
        )
    }

    override fun synchronizeDecoded(entries: Collection<FishingBait>) {
        FishingBaits.reload(mapOf(cobblemonResource("fishing_baits") to entries.toList())  )
    }
}