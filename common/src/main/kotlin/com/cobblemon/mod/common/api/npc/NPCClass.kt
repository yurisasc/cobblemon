/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.npc

import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.MoValue
import com.bedrockk.molang.runtime.value.StringValue
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.npc.configuration.NPCBattleConfiguration
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.asExpressionLike
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.readMapK
import com.cobblemon.mod.common.util.readText
import com.cobblemon.mod.common.util.writeMapK
import com.cobblemon.mod.common.util.writeText
import com.mojang.datafixers.util.Either
import net.minecraft.entity.EntityDimensions
import net.minecraft.network.RegistryByteBuf
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

/**
 * A class of NPC. This can contain a lot of preset information about the NPC's behaviour. Consider this the Pokémon
 * species but for NPCs.
 *
 * @author Hiroku
 * @since August 14th, 2023
 */
class NPCClass {
    @Transient
    lateinit var resourceIdentifier: Identifier

    var names = mutableListOf<MutableText>(lang("npc.name.default"))
    var hitbox = EntityDimensions.changing(0.6F, 1.8F)
    var battleConfiguration = NPCBattleConfiguration()
    var interaction: Either<Identifier, ExpressionLike>? = null
    var variables = mutableMapOf<String, MoValue>()

    fun encode(buffer: RegistryByteBuf) {
        buffer.writeCollection(names) { _, v -> buffer.writeText(v) }
        buffer.writeFloat(this.hitbox.width)
        buffer.writeFloat(this.hitbox.height)
        buffer.writeBoolean(this.hitbox.fixed)
        battleConfiguration.encode(buffer)
        buffer.writeNullable(interaction) { _, value ->
            buffer.writeBoolean(value.map({ true }, { false }))
            buffer.writeString(value.map({ it.toString() }, { it.toString() }))
        }
        buffer.writeMapK(size = IntSize.U_BYTE, map = variables) { (key, value) ->
            buffer.writeString(key)
            buffer.writeString(value.asString())
        }
    }

    fun decode(buffer: RegistryByteBuf) {
        names = buffer.readList { buffer.readText().copy() }.toMutableList()
        val length = buffer.readFloat()
        val width = buffer.readFloat()
        val fixed = buffer.readBoolean()
        hitbox = if (fixed) EntityDimensions.fixed(length, width) else EntityDimensions.changing(length, width)
        battleConfiguration = NPCBattleConfiguration()
        battleConfiguration.decode(buffer)
        interaction = buffer.readNullable {
            if (buffer.readBoolean()) {
                Either.left(Identifier.of(buffer.readString()))
            } else {
                Either.right(buffer.readString().asExpressionLike())
            }
        }
        buffer.readMapK(size = IntSize.U_BYTE, map = variables) {
            val key = buffer.readString()
            val value = buffer.readString()
            if (value.toDoubleOrNull() != null) {
                return@readMapK key to DoubleValue(value.toDouble())
            } else {
                return@readMapK key to StringValue(value)
            }
        }
    }
}