package com.cablemc.pokemoncobbled.common.util

import net.minecraft.util.math.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerWorld
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3d
fun Level.playSoundServer(
    position: Vec3d
    sound: SoundEvent,
    category: SoundSource = SoundSource.NEUTRAL,
    volume: Float = 1F,
    pitch: Float = 1F
) = (this as ServerWorld).playSound(null, position.x, position.y, position.z, sound, category, volume, pitch)

fun <T : ParticleOptions> Level.sendParticlesServer(
    particleType: T,
    position: Vec3d
    particles: Int,
    offset: Vec3d
    speed: Double
) = (this as ServerWorld).sendParticles(particleType, position.x, position.y, position.z, particles, offset.x, offset.y, offset.z, speed)

fun Level.squeezeWithinBounds(pos: BlockPos): BlockPos {
    return if (pos.y < minBuildHeight) {
        BlockPos(pos.x, minBuildHeight, pos.z)
    } else if (pos.y > maxBuildHeight) {
        BlockPos(pos.x, maxBuildHeight, pos.z)
    } else {
        pos
    }
}