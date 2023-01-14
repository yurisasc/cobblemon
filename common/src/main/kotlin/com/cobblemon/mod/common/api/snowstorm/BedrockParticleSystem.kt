package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.util.adapters.ExpressionAdapter
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.google.gson.GsonBuilder
import kotlin.math.floor
import net.minecraft.particle.ParticleEffect
import net.minecraft.util.Identifier

/**
 * This is an interpretation of the Bedrock Edition particle system. The behaviour of the effects are
 * intended to be the same, but the grouping of elements and the transfer JSON format is custom.
 *
 * @author Hiroku
 * @since January 2nd, 2022
 */
class BedrockParticleEffect {
    companion object {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Expression::class.java, ExpressionAdapter)
            .registerTypeAdapter(ParticleEffectCurve::class.java, ParticleEffectCurve.adapter)
            .registerTypeAdapter(ParticleEmitterShape::class.java, ParticleEmitterShape.adapter)
            .registerTypeAdapter(ParticleUVMode::class.java, ParticleUVMode.adapter)
            .registerTypeAdapter(ParticleMotion::class.java, ParticleMotion.adapter)
            .registerTypeAdapter(ParticleEmitterRate::class.java, ParticleEmitterRate.adapter)
            .registerTypeAdapter(ParticleMotionDirection::class.java, ParticleMotionDirection.adapter)
            .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            .create()
    }

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