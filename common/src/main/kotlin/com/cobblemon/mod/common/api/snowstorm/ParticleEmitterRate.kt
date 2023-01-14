package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.serialization.ClassMapAdapter
import com.cobblemon.mod.common.util.getFromJSON

interface ParticleEmitterRate {
    companion object {
        val rates = mutableMapOf<ParticleEmitterRateType, Class<out ParticleEmitterRate>>()
        val adapter = ClassMapAdapter(rates) { ParticleEmitterRateType.values().getFromJSON(it, "type") }

        init {
            rates[ParticleEmitterRateType.INSTANT] = InstantParticleEmitterRate::class.java
        }
    }

    val type: ParticleEmitterRateType
    fun getEmitCount(runtime: MoLangRuntime, started: Boolean): Int
}

enum class ParticleEmitterRateType {
    STEADY,
    INSTANT
}

class InstantParticleEmitterRate : ParticleEmitterRate {
    override val type = ParticleEmitterRateType.INSTANT
    val amount = 1

    override fun getEmitCount(runtime: MoLangRuntime, started: Boolean): Int {
        if (started) {
            return 0
        } else {
            return amount
        }
    }
}