/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.api.net.NetworkPacket
import net.minecraft.server.network.ServerPlayerEntity

interface PokemodImplementation {
    fun isModInstalled(id: String): Boolean
}

interface NetworkDelegate {
    fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket)
    fun sendPacketToServer(packet: NetworkPacket)
    fun <T : NetworkPacket> buildMessage(
        packetClass: Class<T>,
        toServer: Boolean
    ): PokemodNetwork.PreparedMessage<T>
}