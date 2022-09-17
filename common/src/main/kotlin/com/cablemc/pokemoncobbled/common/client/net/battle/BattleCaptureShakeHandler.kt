/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.client.render.pokeball.animation.ShakeAnimation
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleCaptureShakePacket

object BattleCaptureShakeHandler : ClientPacketHandler<BattleCaptureShakePacket> {
    override fun invokeOnClient(packet: BattleCaptureShakePacket, ctx: CobbledNetwork.NetworkContext) {
        val battle = PokemonCobbledClient.battle ?: return
        val (_, activeBattlePokemon) = battle.getPokemonFromPNX(packet.targetPNX)
        val ballState = activeBattlePokemon.ballCapturing ?: return
        ballState.statefulAnimations.add(ShakeAnimation((if (packet.direction) 1F else -1F) * 0.8F))
    }
}