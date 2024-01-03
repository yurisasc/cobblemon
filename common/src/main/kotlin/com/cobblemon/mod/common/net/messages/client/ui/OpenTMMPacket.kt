package com.cobblemon.mod.common.net.messages.client.ui

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

class OpenTMMPacket: NetworkPacket<OpenTMMPacket> {
    override val id = cobblemonResource("open_tm")

    override fun encode(buffer: PacketByteBuf) {

    }

}