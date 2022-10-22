/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.storage.pc

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.client.storage.ClientPC
import com.cablemc.pokemod.common.net.messages.client.storage.pc.InitializePCPacket

object InitializePCHandler : ClientPacketHandler<InitializePCPacket> {
    override fun invokeOnClient(packet: InitializePCPacket, ctx: PokemodNetwork.NetworkContext) {
        PokemodClient.storage.pcStores[packet.storeID] = ClientPC(packet.storeID, packet.boxCount)
    }
}