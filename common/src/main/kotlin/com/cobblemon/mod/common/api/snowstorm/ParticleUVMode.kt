package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.bedrockk.molang.ast.NumberExpression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.codec.CodecMapped
import com.cobblemon.mod.common.api.serialization.ClassMapAdapter
import com.cobblemon.mod.common.util.codec.EXPRESSION_CODEC
import com.cobblemon.mod.common.util.getFromJSON
import com.cobblemon.mod.common.util.resolveInt
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.PacketByteBuf

abstract class ParticleUVMode : CodecMapped {
    companion object : ArbitrarilyMappedSerializableCompanion<ParticleUVMode, ParticleUVModeType>(
        keyFromValue = { it.type },
        stringFromKey = { it.name },
        keyFromString = ParticleUVModeType::valueOf
    ) {
        init {
            registerSubtype(ParticleUVModeType.ANIMATED, AnimatedParticleUVMode::class.java, AnimatedParticleUVMode.CODEC)
            registerSubtype(ParticleUVModeType.STATIC, StaticParticleUVMode::class.java, StaticParticleUVMode.CODEC)
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

class AnimatedParticleUVMode(
    var maxFrame: Expression = NumberExpression(1.0),
    var fps: Expression = NumberExpression(1.0),
    var stretchToLifetime: Boolean = false,
    var loop: Boolean = false
) : ParticleUVMode() {
    override val type = ParticleUVModeType.ANIMATED

    companion object {
        val CODEC: Codec<AnimatedParticleUVMode> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                EXPRESSION_CODEC.fieldOf("maxFrame").forGetter { it.maxFrame },
                EXPRESSION_CODEC.fieldOf("fps").forGetter { it.fps },
                PrimitiveCodec.BOOL.fieldOf("stretchToLifetime").forGetter { it.stretchToLifetime },
                PrimitiveCodec.BOOL.fieldOf("loop").forGetter { it.loop }
            ).apply(instance) { _, maxFrame, fps, stretchToLifetime, loop -> AnimatedParticleUVMode(maxFrame, fps, stretchToLifetime, loop)}
        }
    }

    override fun <T> encode(ops: DynamicOps<T>): DataResult<T> {
        return CODEC.encodeStart(ops, this)
    }

    override fun readFromBuffer(buffer: PacketByteBuf) {
        maxFrame = MoLang.createParser(buffer.readString()).parseExpression()
        fps = MoLang.createParser(buffer.readString()).parseExpression()
        stretchToLifetime = buffer.readBoolean()
        loop = buffer.readBoolean()
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(maxFrame.attributes["string"] as String)
        buffer.writeString(fps.attributes["fps"] as String)
        buffer.writeBoolean(stretchToLifetime)
        buffer.writeBoolean(loop)
    }

    override fun get(moLangRuntime: MoLangRuntime, age: Float, maxAge: Expression): UVDetails {
        TODO("Animated particle mode not yet implemented")
    }
}

class StaticParticleUVMode : ParticleUVMode() {
    override val type = ParticleUVModeType.STATIC

    companion object {
        val CODEC: Codec<StaticParticleUVMode> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { StaticParticleUVMode() }
        }
    }

    override fun get(moLangRuntime: MoLangRuntime, age: Float, maxAge: Expression): UVDetails {
        return UVDetails.set(
            startU = moLangRuntime.resolveInt(startU),
            startV = moLangRuntime.resolveInt(startV),
            uSize = moLangRuntime.resolveInt(uSize),
            vSize = moLangRuntime.resolveInt(vSize)
        )
    }

    override fun <T> encode(ops: DynamicOps<T>): DataResult<T> {
        return CODEC.encodeStart(ops, this)
    }

    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
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