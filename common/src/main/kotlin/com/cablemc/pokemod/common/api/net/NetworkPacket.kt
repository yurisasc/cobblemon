/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.net

import com.cablemc.pokemod.common.PokemodNetwork
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

    fun sendToPlayer(player: ServerPlayerEntity) = PokemodNetwork.sendToPlayer(player, this)
    fun sendToPlayers(players: Iterable<ServerPlayerEntity>) {
        if (players.any()) {
            PokemodNetwork.sendToPlayers(players, this)
        }
    }
    fun sendToAllPlayers() = PokemodNetwork.sendToAllPlayers(this)
    fun sendToServer() = PokemodNetwork.sendToServer(this)
}