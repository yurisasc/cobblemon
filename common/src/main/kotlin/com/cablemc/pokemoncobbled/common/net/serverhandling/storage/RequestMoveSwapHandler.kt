package com.cablemc.pokemoncobbled.forge.common.net.serverhandling.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.forge.common.net.messages.server.RequestMoveSwapPacket
import net.minecraftforge.network.NetworkEvent

object RequestMoveSwapHandler: PacketHandler<RequestMoveSwapPacket> {
    override fun invoke(packet: RequestMoveSwapPacket, ctx: CobbledNetwork.NetworkContext) {
        val player = ctx.player ?: return
        PokemonStoreManager.getParty(player).also { party ->
            party.get(packet.slot)?.also { pkm ->
                pkm.moveSet.swapMove(packet.move1, packet.move2)
                pkm.getMoveSetObservable().emit(pkm.moveSet)
            }
        }
    }
}