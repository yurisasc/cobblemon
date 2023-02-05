/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.effect

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.client.net.ClientPacketHandler
import com.cobblemon.mod.common.client.particle.ParticleStorm
import com.cobblemon.mod.common.client.particle.StaticParticleOrigin
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormParticlePacket
import net.minecraft.client.MinecraftClient

object SpawnSnowstormParticleHandler : ClientPacketHandler<SpawnSnowstormParticlePacket> {
    override fun invokeOnClient(packet: SpawnSnowstormParticlePacket, ctx: CobblemonNetwork.NetworkContext) {
        val origin = StaticParticleOrigin(packet.position)
        val world = MinecraftClient.getInstance().world ?: return
        val storm = ParticleStorm(packet.effect, origin, world)
        MinecraftClient.getInstance().particleManager.addParticle(storm)
    }
}