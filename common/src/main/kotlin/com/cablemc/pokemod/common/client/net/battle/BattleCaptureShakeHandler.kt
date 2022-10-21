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
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.client.render.pokeball.animation.ShakeAnimation
import com.cablemc.pokemod.common.net.messages.client.battle.BattleCaptureShakePacket

object BattleCaptureShakeHandler : ClientPacketHandler<BattleCaptureShakePacket> {
    override fun invokeOnClient(packet: BattleCaptureShakePacket, ctx: PokemodNetwork.NetworkContext) {
        val battle = PokemodClient.battle ?: return
        val (_, activeBattlePokemon) = battle.getPokemonFromPNX(packet.targetPNX)
        val ballState = activeBattlePokemon.ballCapturing ?: return
        ballState.statefulAnimations.add(ShakeAnimation((if (packet.direction) 1F else -1F) * 0.8F))
    }
}