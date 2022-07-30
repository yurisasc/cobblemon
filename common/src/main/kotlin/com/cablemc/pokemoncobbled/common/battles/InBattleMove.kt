package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

class InBattleMove {
    lateinit var id: String
    lateinit var move: String
    var pp: Int = 100
    var maxpp: Int = 100
    var target: MoveTarget = MoveTarget.self
    var disabled: Boolean = false

    companion object {
        fun loadFromBuffer(buffer: PacketByteBuf): InBattleMove {
            return InBattleMove().apply {
                id = buffer.readString()
                move = buffer.readString()
                pp = buffer.readSizedInt(IntSize.U_BYTE)
                maxpp = buffer.readSizedInt(IntSize.U_BYTE)
                target = MoveTarget.values()[buffer.readSizedInt(IntSize.U_BYTE)]
                disabled = buffer.readBoolean()
            }
        }
    }

    fun getTargets(user: ActiveBattlePokemon) = target.targetList(user)
    fun canBeUsed() = (pp > 0 && !disabled) || mustBeUsed() // Second case is like Thrash, forced choice
    fun mustBeUsed() = maxpp == 100 && pp == 100 && target == MoveTarget.self
    fun saveToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(id)
        buffer.writeString(move)
        buffer.writeSizedInt(IntSize.U_BYTE, pp)
        buffer.writeSizedInt(IntSize.U_BYTE, maxpp)
        buffer.writeSizedInt(IntSize.U_BYTE, target.ordinal)
        buffer.writeBoolean(disabled)
    }
}