package com.cablemc.pokemoncobbled.common.api.net

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

/*
 * Simple packet interface to make a more traditional layout for netcode
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
interface NetworkPacket {
    fun encode(buffer: PacketByteBuf)
    fun decode(buffer: PacketByteBuf)

    fun sendToPlayer(player: ServerPlayerEntity) = CobbledNetwork.sendToPlayer(player, this)
    fun sendToPlayers(players: Iterable<ServerPlayerEntity>) {
        if (players.any()) {
            CobbledNetwork.sendToPlayers(players, this)
        }
    }
    fun sendToAllPlayers() = CobbledNetwork.sendToAllPlayers(this)
    fun sendToServer() = CobbledNetwork.sendToServer(this)
}