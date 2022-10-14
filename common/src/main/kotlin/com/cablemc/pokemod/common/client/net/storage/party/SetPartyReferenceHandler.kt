/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.storage.party

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.storage.party.SetPartyReferencePacket

object SetPartyReferenceHandler : ClientPacketHandler<SetPartyReferencePacket> {
    override fun invokeOnClient(packet: SetPartyReferencePacket, ctx: PokemodNetwork.NetworkContext) {
        PokemodClient.storage.setPartyStore(packet.storeID)
    }
}