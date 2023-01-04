package com.cobblemon.mod.common.api.snowstorm

import net.minecraft.util.math.Vec3d

interface ParticleEmitterShape {
    companion object {
        val shapes = mutableMapOf<String, Class<ParticleEmitterShape>>()
    }

    val type: ParticleEmitterShape

    fun getPoint(): Vec3d
}