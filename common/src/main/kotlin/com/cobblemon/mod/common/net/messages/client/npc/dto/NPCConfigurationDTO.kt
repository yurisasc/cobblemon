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
import com.cobblemon.mod.common.util.*
import com.mojang.datafixers.util.Either
import io.netty.buffer.ByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

class NPCConfigurationDTO : Encodable, Decodable {
    var npcName: MutableComponent = "".text()
    var npcClass: ResourceLocation = cobblemonResource("default")
    var battle: NPCBattleConfiguration? = null
    var interaction: Either<ResourceLocation, ExpressionLike>? = null
    var aspects: MutableSet<String> = mutableSetOf()

    constructor()

    constructor(npcEntity: NPCEntity) {
        npcName = npcEntity.name.copy()
        npcClass = npcEntity.npc.resourceIdentifier
        battle = npcEntity.battle
        interaction = npcEntity.interaction
        aspects = npcEntity.appliedAspects
    }

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeText(npcName)
        buffer.writeIdentifier(npcClass)
        buffer.writeNullable(battle) { _, value -> value.encode(buffer) }
        buffer.writeNullable(interaction) { _, value ->
            buffer.writeBoolean(value.map({ true }, { false }))
            buffer.writeString(value.map({ it.toString() }, { it.toString() }))
        }
        buffer.writeCollection(aspects, ByteBuf::writeString)
    }

    override fun decode(buffer: RegistryFriendlyByteBuf) {
        npcName = buffer.readText().copy()
        npcClass = buffer.readIdentifier()
        battle = buffer.readNullable { NPCBattleConfiguration().apply { decode(buffer) } }
        interaction = buffer.readNullable {
            if (buffer.readBoolean()) {
                Either.left(ResourceLocation.parse(buffer.readString()))
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