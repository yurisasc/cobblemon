package com.cablemc.pokemoncobbled.common.client.net.gui

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.gui.startselection.StarterSelectionScreen
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.ui.StarterUIPacket
import net.minecraft.client.MinecraftClient

object StarterUIPacketHandler : ClientPacketHandler<StarterUIPacket> {
    override fun invokeOnClient(packet: StarterUIPacket, ctx: CobbledNetwork.NetworkContext) {
        MinecraftClient.getInstance().setScreen(
            StarterSelectionScreen(
                categories = packet.categories
            )
        )
    }
}