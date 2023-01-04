package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression

class BedrockParticleEmitter {
    val updateExpressions = mutableListOf<Expression>()
    val rate: ParticleEmitterRate = InstantParticleEmitterRate()
}