package com.cablemc.pokemoncobbled.common.util

import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

fun Level.playSoundServer(
    position: Vec3,
    sound: SoundEvent,
    category: SoundSource = SoundSource.NEUTRAL,
    volume: Float = 1F,
    pitch: Float = 1F
) = (this as ServerLevel).playSound(null, position.x, position.y, position.z, sound, category, volume, pitch)

fun <T : ParticleOptions> Level.sendParticlesServer(
    particleType: T,
    position: Vec3,
    particles: Int,
    offset: Vec3,
    speed: Double
) = (this as ServerLevel).sendParticles(particleType, position.x, position.y, position.z, particles, offset.x, offset.y, offset.z, speed)