package com.cablemc.pokemoncobbled.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.server.SendOutPokemonPacket
import net.minecraftforge.fmllegacy.network.NetworkEvent

object SendOutPokemonHandler : PacketHandler<SendOutPokemonPacket> {
    override fun invoke(packet: SendOutPokemonPacket, ctx: NetworkEvent.Context) {
        val player = ctx.sender ?: return
        val slot = packet.slot.takeIf { it >= 0 } ?: return
        ctx.enqueueWork {
            val party = PokemonStoreManager.getParty(player)
            val pokemon = party.get(slot) ?: return@enqueueWork
            if (pokemon.entity == null) {
                pokemon.sendOut(player.getLevel(), player.position())
            }
        }
    }
}