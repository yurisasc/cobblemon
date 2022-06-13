package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

// note: showdown calls it gameType, but in MC GameType would collide with plugins and shit a lot.

object BattleTypes {
    val SINGLES = makeBattleType("singles", actorsPerSide = 1, slotsPerActor = 1)
    val DOUBLES = makeBattleType("doubles", actorsPerSide = 1, slotsPerActor = 2)
    val TRIPLES = makeBattleType("triples", actorsPerSide = 1, slotsPerActor = 3)
    val MULTI = makeBattleType("multi", actorsPerSide = 2, slotsPerActor = 1)
    // maybe one day we can add MULTI-3 for triple battles with 6 fuckers in it, that'd be sick. We could game it with partial actors though

    fun makeBattleType(
        name: String,
        displayName: MutableText = "pokemoncobbled.battle.types.$name".asTranslated(),
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

    companion object {
        fun loadFromBuffer(buffer: PacketByteBuf): BattleType {
            val name = buffer.readString()
            val displayName = buffer.readText()
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
    fun saveToBuffer(buffer: PacketByteBuf): PacketByteBuf {
        buffer.writeString(name)
        buffer.writeText(displayName)
        buffer.writeSizedInt(IntSize.U_BYTE, actorsPerSide)
        buffer.writeSizedInt(IntSize.U_BYTE, slotsPerActor)
        return buffer
    }
}