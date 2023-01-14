package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.ast.NumberExpression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.serialization.ClassMapAdapter
import com.cobblemon.mod.common.util.getFromJSON
import com.cobblemon.mod.common.util.resolveDouble
import net.minecraft.util.math.Vec3d

interface ParticleMotion {
    companion object {
        val motions = mutableMapOf<ParticleMotionType, Class<out ParticleMotion>>()
        val adapter = ClassMapAdapter(motions) { ParticleMotionType.values().getFromJSON(it, "type") }

        init {
            motions[ParticleMotionType.DYNAMIC] = DynamicParticleMotion::class.java
            motions[ParticleMotionType.STATIC] = StaticParticleMotion::class.java
        }
    }

    val type: ParticleMotionType
    fun getInitialVelocity(runtime: MoLangRuntime, particlePos: Vec3d, emitterPos: Vec3d): Vec3d
    fun getAcceleration(runtime: MoLangRuntime, velocity: Vec3d): Vec3d
}

enum class ParticleMotionType {
    DYNAMIC,
    PARAMETRIC, // TODO figure out how that even works
    STATIC
}


class DynamicParticleMotion : ParticleMotion {
    override val type = ParticleMotionType.DYNAMIC
    val direction: ParticleMotionDirection = InwardsMotionDirection()
    val speed: Expression = NumberExpression(0.0)
    val acceleration: Triple<Expression, Expression, Expression> = Triple(NumberExpression(0.0), NumberExpression(0.0), NumberExpression(0.0))
    val drag: Expression = NumberExpression(0.0)

    override fun getInitialVelocity(runtime: MoLangRuntime, particlePos: Vec3d, emitterPos: Vec3d): Vec3d {
        return direction.getDirectionVector(runtime, emitterPos, particlePos).multiply(runtime.resolveDouble(speed))
    }

    override fun getAcceleration(runtime: MoLangRuntime, velocity: Vec3d): Vec3d {
        val acceleration = Vec3d(
            runtime.resolveDouble(acceleration.first),
            runtime.resolveDouble(acceleration.second),
            runtime.resolveDouble(acceleration.third)
        )

        val nextVelocity = velocity.add(acceleration)
        val drag = nextVelocity.normalize().multiply(runtime.resolveDouble(drag))
        return if (drag.length() > nextVelocity.length()) {
            Vec3d.ZERO
        } else {
            nextVelocity.subtract(drag)
        }
    }
}

interface ParticleMotionDirection {
    companion object {
        val directions = mutableMapOf<ParticleMotionDirectionType, Class<out ParticleMotionDirection>>()
        val adapter = ClassMapAdapter(directions) { ParticleMotionDirectionType.values().getFromJSON(it, "type") }

        init {
            directions[ParticleMotionDirectionType.INWARDS] = InwardsMotionDirection::class.java
            directions[ParticleMotionDirectionType.OUTWARDS] = OutwardsMotionDirection::class.java
            directions[ParticleMotionDirectionType.CUSTOM] = CustomMotionDirection::class.java
        }
    }
    val type: ParticleMotionDirectionType
    fun getDirectionVector(runtime: MoLangRuntime, emitterPos: Vec3d, particlePos: Vec3d): Vec3d
}

class InwardsMotionDirection : ParticleMotionDirection {
    override val type = ParticleMotionDirectionType.INWARDS
    override fun getDirectionVector(runtime: MoLangRuntime, emitterPos: Vec3d, particlePos: Vec3d) = emitterPos.subtract(particlePos).normalize()
}

class OutwardsMotionDirection : ParticleMotionDirection {
    override val type = ParticleMotionDirectionType.OUTWARDS
    override fun getDirectionVector(runtime: MoLangRuntime, emitterPos: Vec3d, particlePos: Vec3d) = particlePos.subtract(emitterPos).normalize()
}

class CustomMotionDirection : ParticleMotionDirection {
    override val type = ParticleMotionDirectionType.CUSTOM
    val direction: Triple<Expression, Expression, Expression> = Triple(NumberExpression(0.0), NumberExpression(0.0), NumberExpression(0.0))

    override fun getDirectionVector(runtime: MoLangRuntime, emitterPos: Vec3d, particlePos: Vec3d): Vec3d {
        return Vec3d(
            runtime.resolveDouble(direction.first),
            runtime.resolveDouble(direction.second),
            runtime.resolveDouble(direction.third)
        )
    }
}

enum class ParticleMotionDirectionType {
    CUSTOM,
    INWARDS,
    OUTWARDS
}

class StaticParticleMotion : ParticleMotion {
    @Transient
    override val type = ParticleMotionType.STATIC

    override fun getInitialVelocity(runtime: MoLangRuntime, particlePos: Vec3d, emitterPos: Vec3d) = Vec3d.ZERO
    override fun getAcceleration(runtime: MoLangRuntime, velocity: Vec3d) = Vec3d.ZERO
}