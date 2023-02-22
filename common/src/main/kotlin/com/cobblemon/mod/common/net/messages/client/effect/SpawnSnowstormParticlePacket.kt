/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.effect

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.snowstorm.BedrockParticleEffect
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

/**
 * A packet sent to the client to spawn a [BedrockParticleEffect] at the specified coordinates and rotation.
 *
 * Handled by [com.cobblemon.mod.common.client.net.effect.SpawnSnowstormParticleHandler].
 *
 * @author Hiroku
 * @since January 21st, 2022
 */
class SpawnSnowstormParticlePacket(
    val effect: BedrockParticleEffect,
    val position: Vec3d,
    val yawDegrees: Float,
    val pitchDegrees: Float
) : NetworkPacket<SpawnSnowstormParticlePacket> {
    override val id = ID
    companion object {
        val ID = cobblemonResource("spawn_snowstorm_particle")
        fun decode(buffer: PacketByteBuf): SpawnSnowstormParticlePacket {
            return SpawnSnowstormParticlePacket(
                effect = BedrockParticleEffect().also { it.readFromBuffer(buffer) },
                position = Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
                yawDegrees = buffer.readFloat(),
                pitchDegrees = buffer.readFloat()
            )
        }
    }
    override fun encode(buffer: PacketByteBuf) {
        effect.writeToBuffer(buffer)
        buffer.writeDouble(position.x)
        buffer.writeDouble(position.y)
        buffer.writeDouble(position.z)
        buffer.writeFloat(yawDegrees)
        buffer.writeFloat(pitchDegrees)
    }
}