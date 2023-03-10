package com.cobblemon.mod.common.net.messages.client.trade

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

/**
 * Packet sent to the client to notify a player that someone requested to trade with them.
 *
 * @author Hiroku
 * @since March 6th, 2023
 */
class TradeNotificationPacket(val traderName: MutableText): NetworkPacket<TradeNotificationPacket> {
    override val id = ID
    override fun encode(buffer: PacketByteBuf) {
        buffer.writeText(traderName)
    }
    companion object {
        val ID = cobblemonResource("trade_notification")
        fun decode(buffer: PacketByteBuf) = TradeNotificationPacket(buffer.readText().copy())
    }
}