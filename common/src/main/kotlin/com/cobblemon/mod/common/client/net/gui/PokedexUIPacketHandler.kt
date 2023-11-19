package com.cobblemon.mod.common.client.net.gui

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.client.gui.pokedex.PokedexScreen
import com.cobblemon.mod.common.client.net.ClientPacketHandler
import com.cobblemon.mod.common.net.messages.client.ui.PokedexUIPacket
import net.minecraft.client.MinecraftClient

class PokedexUIPacketHandler : ClientPacketHandler<PokedexUIPacket> {
    override fun invokeOnClient(packet: PokedexUIPacket, ctx: CobblemonNetwork.NetworkContext) {
        MinecraftClient.getInstance().setScreen(PokedexScreen(packet.pokedex))
    }
}