package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * Tells the client to terminate its battle reference
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleEndHandler].
 *
 * @author Hiroku
 * @since May 6th, 2022
 */
class BattleEndPacket : NetworkPacket {
    override fun encode(buffer: PacketByteBuf) {}
    override fun decode(buffer: PacketByteBuf) {}
}