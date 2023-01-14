package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.ast.NumberExpression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.serialization.ClassMapAdapter
import com.cobblemon.mod.common.util.getFromJSON
import com.cobblemon.mod.common.util.resolveInt

abstract class ParticleUVMode {
    companion object {
        val uvModes = mutableMapOf<ParticleUVModeType, Class<out ParticleUVMode>>()
        val adapter = ClassMapAdapter(uvModes) { ParticleUVModeType.values().getFromJSON(it, "type") }

        init {
            uvModes[ParticleUVModeType.ANIMATED] = AnimatedParticleUVMode::class.java
            uvModes[ParticleUVModeType.STATIC] = StaticParticleUVMode::class.java
        }
    }

    abstract val type: ParticleUVModeType
    val startU: Expression = NumberExpression(0.0)
    val startV: Expression = NumberExpression(0.0)
    val uSize: Expression = NumberExpression(8.0)
    val vSize: Expression = NumberExpression(8.0)

    abstract fun get(moLangRuntime: MoLangRuntime, age: Float, maxAge: Expression): UVDetails
}

enum class ParticleUVModeType {
    STATIC,
    ANIMATED
}

class AnimatedParticleUVMode : ParticleUVMode() {
    override val type = ParticleUVModeType.ANIMATED
    val maxFrame: Expression = NumberExpression(1.0)
    val fps: Expression = NumberExpression(1.0)
    val stretchToLifetime = false
    val loop = false

    override fun get(moLangRuntime: MoLangRuntime, age: Float, maxAge: Expression): UVDetails {
        TODO("Animated particle mode not yet implemented")
    }
}

class StaticParticleUVMode : ParticleUVMode() {
    override val type = ParticleUVModeType.STATIC

    override fun get(moLangRuntime: MoLangRuntime, age: Float, maxAge: Expression): UVDetails {
        return UVDetails.set(
            startU = moLangRuntime.resolveInt(startU),
            startV = moLangRuntime.resolveInt(startV),
            uSize = moLangRuntime.resolveInt(uSize),
            vSize = moLangRuntime.resolveInt(vSize)
        )
    }
}

object UVDetails {
    var startU: Int = 0
    var startV: Int = 0
    var uSize: Int = 0
    var vSize: Int = 0

    fun set(startU: Int, startV: Int, uSize: Int, vSize: Int): UVDetails {
        this.startU = startU
        this.startV = startV
        this.uSize = uSize
        this.vSize = vSize
        return this
    }
}