package com.cobblemon.mod.common.client.net.gui

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.net.messages.client.ui.InteractPokemonUIPacket
import com.cobblemon.mod.common.net.messages.client.ui.OpenTMMPacket
import net.minecraft.client.MinecraftClient

object OpenTMMPacketHandler : ClientNetworkPacketHandler<OpenTMMPacket>
{
    override fun handle(packet: OpenTMMPacket, client: MinecraftClient) {
        //client.setScreen()
    }
}