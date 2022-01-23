package com.cablemc.pokemoncobbled.client.net.gui

import com.cablemc.pokemoncobbled.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.ui.SummaryUIPacket
import net.minecraft.client.Minecraft
import net.minecraftforge.fmllegacy.network.NetworkEvent

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