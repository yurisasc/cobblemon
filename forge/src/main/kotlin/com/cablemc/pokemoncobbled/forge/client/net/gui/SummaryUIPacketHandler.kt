package com.cablemc.pokemoncobbled.forge.client.net.gui

import com.cablemc.pokemoncobbled.forge.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.forge.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.ui.SummaryUIPacket
import net.minecraft.client.Minecraft
import net.minecraftforge.network.NetworkEvent

object SummaryUIPacketHandler: ClientPacketHandler<SummaryUIPacket> {
    override fun invokeOnClient(packet: SummaryUIPacket, ctx: NetworkEvent.Context) {
        Minecraft.getInstance().setScreen(
            Summary(
                pokemon = packet.pokemonArray.toTypedArray(),
                editable = packet.editable
            )
        )
    }
}