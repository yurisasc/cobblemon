package com.cablemc.pokemoncobbled.common.net.serverhandling.storage.pc

import com.cablemc.pokemoncobbled.common.CobbledNetwork.NetworkContext
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.RemoveClientPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.MoveClientPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.pc.ClosePCPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePartyPokemonToPCPacket
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketHandler
import net.minecraft.server.network.ServerPlayerEntity

object MovePartyPokemonToPCHandler : ServerPacketHandler<MovePartyPokemonToPCPacket> {
    override fun invokeOnServer(packet: MovePartyPokemonToPCPacket, ctx: NetworkContext, player: ServerPlayerEntity) {
        val party = PokemonCobbled.storage.getParty(player)
        val pc = PCLinkManager.getPC(player) ?: return run { ClosePCPacket().sendToPlayer(player) }
        val pokemon = party[packet.partyPosition] ?: return
        if (pokemon.uuid != packet.pokemonID) {
            return
        }
        if (party.filterNotNull().size == 1 && PokemonCobbled.config.preventCompletePartyDeposit) {
            MoveClientPartyPokemonPacket(party.uuid, pokemon.uuid, packet.partyPosition).sendToPlayer(player)
            RemoveClientPokemonPacket(pc, pokemon.uuid).sendToPlayer(player)
            return
        }
        val pcPosition = packet.pcPosition?.takeIf { pc[it] == null } ?: pc.getFirstAvailablePosition() ?: return
        party.remove(packet.partyPosition)
        pc[pcPosition] = pokemon
    }
}