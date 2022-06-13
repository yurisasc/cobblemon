package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

class InBattleMove(
    val id: String,
    val move: String,
    val pp: Int,
    val maxpp: Int,
    val target: MoveTarget,
    val disabled: Boolean
) {
    companion object {
        fun loadFromBuffer(buffer: PacketByteBuf): InBattleMove {
            return InBattleMove(
                id = buffer.readString(),
                move = buffer.readString(),
                pp = buffer.readSizedInt(IntSize.U_BYTE),
                maxpp = buffer.readSizedInt(IntSize.U_BYTE),
                target = MoveTarget.values()[buffer.readSizedInt(IntSize.U_BYTE)],
                disabled = buffer.readBoolean()
            )
        }
    }

    fun getTargets(user: ActiveBattlePokemon) = target.targetList(user)
    fun canBeUsed() = pp > 0 && !disabled
    fun saveToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(id)
        buffer.writeString(move)
        buffer.writeSizedInt(IntSize.U_BYTE, pp)
        buffer.writeSizedInt(IntSize.U_BYTE, maxpp)
        buffer.writeSizedInt(IntSize.U_BYTE, target.ordinal)
        buffer.writeBoolean(disabled)
    }
}