package com.cobblemon.mod.common.api.snowstorm

interface ParticleUVMode {
    companion object {
        val uvModes = mutableMapOf<ParticleUVModeType, Class<out ParticleUVMode>>()

        init {
            uvModes[ParticleUVModeType.ANIMATED] = AnimatedParticleUVMode::class.java
        }
    }

    val type: ParticleUVModeType
    val startU: Int
    val startV: Int
    val uSize: Int
    val vSize: Int
}

enum class ParticleUVModeType {
    STATIC,
    ANIMATED
}

class AnimatedParticleUVMode : ParticleUVMode {
    override val type = ParticleUVModeType.ANIMATED
    override val startU: Int = 0
    override val startV: Int = 0
    override val uSize: Int = 8
    override val vSize: Int = 8


}