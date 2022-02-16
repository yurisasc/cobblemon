package com.cablemc.pokemoncobbled.forge.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.forge.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.forge.common.net.PacketHandler
import com.cablemc.pokemoncobbled.forge.common.net.messages.server.RequestMoveSwapPacket
import net.minecraftforge.network.NetworkEvent

object RequestMoveSwapHandler: PacketHandler<RequestMoveSwapPacket> {
    override fun invoke(packet: RequestMoveSwapPacket, ctx: NetworkEvent.Context) {
        val player = ctx.sender ?: return
        PokemonStoreManager.getParty(player).also { party ->
            party.get(packet.slot)?.also { pkm ->
                pkm.moveSet.swapMove(packet.move1, packet.move2)
                pkm.getMoveSetObservable().emit(pkm.moveSet)
            }
        }
    }
}