package com.cablemc.pokemoncobbled.common.client.net

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.starter.ClientPlayerData
import com.cablemc.pokemoncobbled.common.net.messages.client.starter.SetClientPlayerDataPacket

object SetClientPlayerDataHandler : ClientPacketHandler<SetClientPlayerDataPacket> {
    override fun invokeOnClient(packet: SetClientPlayerDataPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.clientPlayerData = ClientPlayerData(
            promptStarter = packet.promptStarter,
            starterLocked = packet.starterLocked,
            starterSelected = packet.starterSelected,
            starterUUID = packet.starterUUID
        )
    }
}