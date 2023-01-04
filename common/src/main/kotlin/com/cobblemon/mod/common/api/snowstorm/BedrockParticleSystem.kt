package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import kotlin.math.floor
import net.minecraft.util.Identifier

/**
 * This is an interpretation of the Bedrock Edition particle system. The behaviour of the effects are
 * intended to be the same, but the grouping of elements and the transfer JSON format is custom.
 *
 * @author Hiroku
 * @since January 2nd, 2022
 */
class BedrockParticleEffect {
    val id = Identifier("minecraft:particle_effect")
    var emitter = BedrockParticleEmitter()
    var particle = BedrockParticle()
    var curves = mutableListOf<ParticleEffectCurve>()
    val space = ParticleSpace()
    val startVariables = mutableListOf<Expression>()
}

interface ParticleStormLifetime {
    fun pulse(particleLifetime: Float): ParticleEmitterAction
}

class LoopingStormLifetime : ParticleStormLifetime {
    val activeTime = 1F
    val sleepTime = 0F
    @Transient
    var loopTimes = 1

    override fun pulse(particleLifetime: Float): ParticleEmitterAction {
        val interval = activeTime + sleepTime
        val completedTimes = floor(particleLifetime / interval).toInt()
        if (completedTimes > loopTimes) {
            return ParticleEmitterAction.STOP
        }
        val displacement = particleLifetime % interval
        return if (displacement > activeTime) {
            ParticleEmitterAction.GO
        } else {
            ParticleEmitterAction.NOTHING
        }
    }
}

class OnceStormLifetime : ParticleStormLifetime {
    val activeTime = 1F

    override fun pulse(particleLifetime: Float): ParticleEmitterAction {
        return if (particleLifetime < activeTime) {
            ParticleEmitterAction.GO
        } else {
            ParticleEmitterAction.STOP
        }
    }
}

enum class ParticleEmitterAction {
    NOTHING,
    GO,
    STOP
}