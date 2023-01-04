package com.cobblemon.mod.common.api.snowstorm

import net.minecraft.util.Identifier

interface ParticleEmitterRate {
    companion object {
        val rates = mutableMapOf<String, Class<ParticleEmitterRate>>()
    }

    val type: ParticleEmitterRateType
    fun getEmitCount(storm: ParticleStorm): Int
}

enum class ParticleEmitterRateType {
    STEADY,
    INSTANT
}

class InstantParticleEmitterRate : ParticleEmitterRate {
    override val type = ParticleEmitterRateType.INSTANT
    val amount = 1

    override fun getEmitCount(storm: ParticleStorm): Int {
        if (storm.started) {
            return 0
        } else {
            return amount
        }
    }
}