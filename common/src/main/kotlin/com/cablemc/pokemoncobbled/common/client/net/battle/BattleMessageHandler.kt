package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.battle.widgets.BattleMessagePane
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleMessagePacket
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Language

object BattleMessageHandler : ClientPacketHandler<BattleMessagePacket> {
    override fun invokeOnClient(packet: BattleMessagePacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
        val textRenderer = MinecraftClient.getInstance().textRenderer
        for (message in packet.messages) {
            val lines = Language.getInstance().reorder(textRenderer.textHandler.wrapLines(message, BattleMessagePane.LINE_WIDTH, message.style))
            battle.messages.add(lines)
        }
    }
}