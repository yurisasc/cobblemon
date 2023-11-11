package com.cobblemon.mod.common.pokemon.summaryvalue

import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

class SummaryValue(
    val id: String,
    val displayName: String,
    val maxValue: Int = 100,
    var currentValue: Int = 0,
    val barRed: Number = 0.92,
    val barGreen: Number = 0.7,
    val barBlue: Number = 0.28,
) {
    fun encode(buffer: PacketByteBuf) {
        buffer.writeString(id)
        buffer.writeString(displayName)
        buffer.writeSizedInt(IntSize.INT, maxValue)
        buffer.writeSizedInt(IntSize.INT, currentValue)
    }

    fun decode(buffer: PacketByteBuf) {
        buffer.readString()
        buffer.readString()
        buffer.readSizedInt(IntSize.INT)
        this.currentValue = buffer.readSizedInt(IntSize.INT)
    }
}