/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles

import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import com.cobblemon.mod.common.util.writeString
import io.netty.buffer.ByteBuf
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs

// note: showdown calls it gameType, but in MC GameType would collide with plugins and shit a lot.

object BattleTypes {
    val SINGLES = makeBattleType("singles", actorsPerSide = 1, slotsPerActor = 1)
    val DOUBLES = makeBattleType("doubles", actorsPerSide = 1, slotsPerActor = 2)
    val TRIPLES = makeBattleType("triples", actorsPerSide = 1, slotsPerActor = 3)
    val MULTI = makeBattleType("multi", actorsPerSide = 2, slotsPerActor = 1)
    // maybe one day we can add MULTI-3 for triple battles with 6 fuckers in it, that'd be sick. We could game it with partial actors though

    fun makeBattleType(
        name: String,
        displayName: MutableText = lang("battle.types.$name"),
        actorsPerSide: Int,
        slotsPerActor: Int
    ) = object : BattleType {
        override val name = name
        override val displayName = displayName
        override val actorsPerSide = actorsPerSide
        override val slotsPerActor = slotsPerActor
    }
}

interface BattleType {
    val name: String
    val displayName: MutableText
    val actorsPerSide: Int
    val slotsPerActor: Int

    val pokemonPerSide: Int
        get() = actorsPerSide * slotsPerActor

    companion object {
        fun loadFromBuffer(buffer: PacketByteBuf): BattleType {
            val name = buffer.readString()
            val displayName = TextCodecs.PACKET_CODEC.decode(buffer)
            val actorsPerSide = buffer.readSizedInt(IntSize.U_BYTE)
            val slotsPerActor = buffer.readSizedInt(IntSize.U_BYTE)
            return BattleTypes.makeBattleType(
                name = name,
                displayName = displayName.copy(),
                actorsPerSide = actorsPerSide,
                slotsPerActor = slotsPerActor
            )
        }
    }
    fun saveToBuffer(buffer: ByteBuf): ByteBuf {
        buffer.writeString(name)
        TextCodecs.PACKET_CODEC.encode(buffer, displayName)
        buffer.writeSizedInt(IntSize.U_BYTE, actorsPerSide)
        buffer.writeSizedInt(IntSize.U_BYTE, slotsPerActor)
        return buffer
    }
}