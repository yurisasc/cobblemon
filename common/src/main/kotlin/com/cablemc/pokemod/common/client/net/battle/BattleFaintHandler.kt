/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.battle

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.battle.animations.MoveTileOffscreenAnimation
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.BattleFaintPacket

object BattleFaintHandler : ClientPacketHandler<BattleFaintPacket> {
    override fun invokeOnClient(packet: BattleFaintPacket, ctx: PokemodNetwork.NetworkContext) {
        PokemodClient.battle?.getPokemonFromPNX(packet.pnx)?.second?.animations?.add(MoveTileOffscreenAnimation())
    }
}