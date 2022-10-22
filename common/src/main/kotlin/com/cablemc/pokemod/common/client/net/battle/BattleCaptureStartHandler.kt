/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.battle

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.pokeball.PokeBalls
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.battle.ClientBallDisplay
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.BattleCaptureStartPacket

object BattleCaptureStartHandler : ClientPacketHandler<BattleCaptureStartPacket> {
    override fun invokeOnClient(packet: BattleCaptureStartPacket, ctx: PokemodNetwork.NetworkContext) {
        val battle = PokemodClient.battle ?: return
        val targetPokemon = battle.getPokemonFromPNX(packet.targetPNX)
        val tile = targetPokemon.second
        val pokeBall = PokeBalls.getPokeBall(packet.pokeBallType) ?: PokeBalls.POKE_BALL
        tile.ballCapturing = ClientBallDisplay(pokeBall).also { it.start() }
    }
}