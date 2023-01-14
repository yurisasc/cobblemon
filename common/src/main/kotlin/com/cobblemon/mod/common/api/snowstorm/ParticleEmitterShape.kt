package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.ast.NumberExpression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.serialization.ClassMapAdapter
import com.cobblemon.mod.common.util.getFromJSON
import com.cobblemon.mod.common.util.math.convertSphericalToCartesian
import com.cobblemon.mod.common.util.resolveDouble
import com.cobblemon.mod.common.util.resolveVec3d
import kotlin.random.Random
import net.minecraft.util.math.Vec3d

interface ParticleEmitterShape {
    companion object {
        val shapes = mutableMapOf<ParticleEmitterShapeType, Class<out ParticleEmitterShape>>()
        val adapter = ClassMapAdapter(shapes) { ParticleEmitterShapeType.values().getFromJSON(it, "type")}

        init {
            shapes[ParticleEmitterShapeType.SPHERE] = SphereParticleEmitterShape::class.java
        }
    }

    val type: ParticleEmitterShapeType

    fun getNewParticlePosition(runtime: MoLangRuntime): Vec3d
    fun getCenter(runtime: MoLangRuntime): Vec3d
}

enum class ParticleEmitterShapeType {
    SPHERE,
    POINT,
    BOX,
    DISC,
    ENTITY_BOUNDING_BOX
}

class SphereParticleEmitterShape : ParticleEmitterShape {
    override val type = ParticleEmitterShapeType.SPHERE
    val offset: Triple<Expression, Expression, Expression> = Triple(NumberExpression(0.0), NumberExpression(0.0), NumberExpression(0.0))
    val radius: Expression = NumberExpression(0.0)
    val surfaceOnly = false

    override fun getCenter(runtime: MoLangRuntime): Vec3d {
        return runtime.resolveVec3d(offset)
    }

    override fun getNewParticlePosition(runtime: MoLangRuntime): Vec3d {
        val radius = runtime.resolveDouble(radius) * if (surfaceOnly) 1.0 else Random.Default.nextDouble()
        val theta = Math.PI * 2 * Random.Default.nextDouble()
        val psi = Math.PI * 2 * Random.Default.nextDouble()
        return getCenter(runtime).add(convertSphericalToCartesian(radius = radius, theta = theta, psi = psi))
    }
}