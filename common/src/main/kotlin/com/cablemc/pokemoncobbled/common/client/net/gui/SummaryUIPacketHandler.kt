package com.cablemc.pokemoncobbled.common.client.net.gui

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.ui.SummaryUIPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.MinecraftClient

object SummaryUIPacketHandler: ClientPacketHandler<SummaryUIPacket> {
    override fun invokeOnClient(packet: SummaryUIPacket, ctx: CobbledNetwork.NetworkContext) {
        MinecraftClient.getInstance().setScreen(
            Summary(
                pokemon = packet.pokemonArray.toTypedArray(),
                editable = packet.editable
            )
        )
    }
}