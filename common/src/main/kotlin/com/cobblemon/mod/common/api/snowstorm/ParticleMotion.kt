package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import net.minecraft.util.math.Vec3d

interface ParticleMotion {
    companion object {
        val motions = mutableMapOf<String, Class<ParticleMotion>>()
    }

    val type: ParticleMotionType

    fun getVelocity(currentVelocity: Vec3d?): Expression
}