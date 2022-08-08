package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.text.lightPurple
import com.cablemc.pokemoncobbled.common.client.keybind.currentKey
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.PartySendBinding
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.ChallengeNotificationPacket
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.client.MinecraftClient

object ChallengeNotificationHandler : ClientPacketHandler<ChallengeNotificationPacket> {
    override fun invokeOnClient(packet: ChallengeNotificationPacket, ctx: CobbledNetwork.NetworkContext) {
        MinecraftClient.getInstance().player?.sendServerMessage(
            lang(
                "challenge.receiver",
                packet.challengerName,
                PartySendBinding.currentKey().localizedText
            ).lightPurple()
        )
    }
}