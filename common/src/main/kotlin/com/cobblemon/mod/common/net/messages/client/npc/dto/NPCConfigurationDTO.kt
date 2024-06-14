/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.npc.dto

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.npc.NPCClasses
import com.cobblemon.mod.common.api.npc.configuration.NPCBattleConfiguration
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.entity.npc.NPCEntity
import com.cobblemon.mod.common.util.asExpressionLike
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.readText
import com.cobblemon.mod.common.util.writeText
import com.mojang.datafixers.util.Either
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.RegistryByteBuf
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class NPCConfigurationDTO : Encodable, Decodable {
    var npcName: MutableText = "".text()
    var npcClass: Identifier = cobblemonResource("default")
    var battle: NPCBattleConfiguration? = null
    var interaction: Either<Identifier, ExpressionLike>? = null
    var aspects: MutableSet<String> = mutableSetOf()

    constructor()

    constructor(npcEntity: NPCEntity) {
        npcName = npcEntity.name.copy()
        npcClass = npcEntity.npc.resourceIdentifier
        battle = npcEntity.battle
        interaction = npcEntity.interaction
        aspects = npcEntity.appliedAspects
    }

    override fun encode(buffer: RegistryByteBuf) {
        buffer.writeText(npcName)
        buffer.writeIdentifier(npcClass)
        buffer.writeNullable(battle) { _, value -> value.encode(buffer) }
        buffer.writeNullable(interaction) { _, value ->
            buffer.writeBoolean(value.map({ true }, { false }))
            buffer.writeString(value.map({ it.toString() }, { it.toString() }))
        }
        buffer.writeCollection(aspects, PacketByteBuf::writeString)
    }

    override fun decode(buffer: RegistryByteBuf) {
        npcName = buffer.readText().copy()
        npcClass = buffer.readIdentifier()
        battle = buffer.readNullable { NPCBattleConfiguration().apply { decode(buffer) } }
        interaction = buffer.readNullable {
            if (buffer.readBoolean()) {
                Either.left(Identifier(buffer.readString()))
            } else {
                Either.right(buffer.readString().asExpressionLike())
            }
        }
        aspects = buffer.readList { buffer.readString() }.toMutableSet()
    }

    fun apply(entity: NPCEntity) {
        val npcClass =  NPCClasses.getByIdentifier(npcClass) ?: return Cobblemon.LOGGER.error("Failed to apply NPC class $npcClass")
        entity.customName = npcName.copy()
        entity.npc = npcClass
        entity.battle = battle
        entity.interaction = interaction
        entity.appliedAspects.clear()
        entity.appliedAspects.addAll(aspects)
    }
}