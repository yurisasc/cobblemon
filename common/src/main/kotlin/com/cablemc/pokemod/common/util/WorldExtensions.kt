/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

import net.minecraft.particle.ParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

fun World.playSoundServer(
    position: Vec3d,
    sound: SoundEvent,
    category: SoundCategory = SoundCategory.NEUTRAL,
    volume: Float = 1F,
    pitch: Float = 1F
) = (this as ServerWorld).playSound(null, position.x, position.y, position.z, sound, category, volume, pitch)

fun <T : ParticleEffect> World.sendParticlesServer(
    particleType: T,
    position: Vec3d,
    particles: Int,
    offset: Vec3d,
    speed: Double
) = (this as ServerWorld).spawnParticles(particleType, position.x, position.y, position.z, particles, offset.x, offset.y, offset.z, speed)

fun World.squeezeWithinBounds(pos: BlockPos): BlockPos {
    return if (pos.y < bottomY) {
        BlockPos(pos.x, bottomY, pos.z)
    } else if (pos.y > topY) {
        BlockPos(pos.x, topY, pos.z)
    } else {
        pos
    }
}