/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.battle

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.net.ClientPacketHandler
import com.cobblemon.mod.common.client.render.pokeball.animation.ShakeAnimation
import com.cobblemon.mod.common.net.messages.client.battle.BattleCaptureShakePacket

object BattleCaptureShakeHandler : ClientPacketHandler<BattleCaptureShakePacket> {
    override fun invokeOnClient(packet: BattleCaptureShakePacket, ctx: CobblemonNetwork.NetworkContext) {
        val battle = CobblemonClient.battle ?: return
        val (_, activeBattlePokemon) = battle.getPokemonFromPNX(packet.targetPNX)
        val ballState = activeBattlePokemon.ballCapturing ?: return
        ballState.statefulAnimations.add(ShakeAnimation((if (packet.direction) 1F else -1F) * 0.8F))
    }
}