package com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc

import com.cablemc.pokemoncobbled.common.CobbledNetwork.NetworkContext
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object MovePCPokemonToPartyHandler : ServerPacketHandler<MovePCPokemonToPartyPacket> {
    override fun invokeOnServer(packet: MovePCPokemonToPartyPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val party = PokemonCobbled.storage.getParty(player)
        val pc = PCLinkManager.getPC(player) ?: return run { ClosePCPacket().sendToPlayer(player) }

        val pokemon = pc[packet.pcPosition] ?: return
        if (pokemon.uuid != packet.pokemonID) {
            return
        }

        val partyPosition = packet.partyPosition?.takeIf { party[it] == null } ?: party.getFirstAvailablePosition() ?: return
        pc.remove(packet.pcPosition)
        party[partyPosition] = pokemon
    }
}