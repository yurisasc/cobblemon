package com.cablemc.pokemoncobbled.common.client.net.starter

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.startselection.StarterSelectionScreen
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.starter.OpenStarterUIPacket
import net.minecraft.client.MinecraftClient

object StarterUIPacketHandler : ClientPacketHandler<OpenStarterUIPacket> {
    override fun invokeOnClient(packet: OpenStarterUIPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.checkedStarterScreen = true
        MinecraftClient.getInstance().setScreen(StarterSelectionScreen(categories = packet.categories))
    }
}