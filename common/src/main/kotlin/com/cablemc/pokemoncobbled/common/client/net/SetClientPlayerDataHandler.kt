/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.net

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.starter.ClientPlayerData
import com.cablemc.pokemoncobbled.common.net.messages.client.starter.SetClientPlayerDataPacket

object SetClientPlayerDataHandler : ClientPacketHandler<SetClientPlayerDataPacket> {
    override fun invokeOnClient(packet: SetClientPlayerDataPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.clientPlayerData = ClientPlayerData(
            promptStarter = packet.promptStarter,
            starterLocked = packet.starterLocked,
            starterSelected = packet.starterSelected,
            starterUUID = packet.starterUUID
        )
    }
}