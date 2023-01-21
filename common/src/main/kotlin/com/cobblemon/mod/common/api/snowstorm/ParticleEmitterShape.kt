package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.bedrockk.molang.ast.NumberExpression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.codec.CodecMapped
import com.cobblemon.mod.common.api.codec.MappedCodec
import com.cobblemon.mod.common.util.codec.EXPRESSION_CODEC
import com.cobblemon.mod.common.util.math.convertSphericalToCartesian
import com.cobblemon.mod.common.util.resolveDouble
import com.cobblemon.mod.common.util.resolveVec3d
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import kotlin.random.Random
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

interface ParticleEmitterShape : CodecMapped {
    companion object : ArbitrarilyMappedSerializableCompanion<ParticleEmitterShape, ParticleEmitterShapeType>(
        keyFromString = ParticleEmitterShapeType::valueOf,
        stringFromKey = { it.name },
        keyFromValue = { it.type }
    ) {
        init {
            registerSubtype(ParticleEmitterShapeType.SPHERE, SphereParticleEmitterShape::class.java, SphereParticleEmitterShape.CODEC)
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

class SphereParticleEmitterShape(
    var offset: Triple<Expression, Expression, Expression> = Triple(
        NumberExpression(0.0),
        NumberExpression(0.0),
        NumberExpression(0.0)
    ),
    var radius: Expression = NumberExpression(0.0),
    var surfaceOnly: Boolean = false
) : ParticleEmitterShape {
    companion object {
        val CODEC: Codec<SphereParticleEmitterShape> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                EXPRESSION_CODEC.fieldOf("offsetX").forGetter { it.offset.first },
                EXPRESSION_CODEC.fieldOf("offsetY").forGetter { it.offset.second },
                EXPRESSION_CODEC.fieldOf("offsetZ").forGetter { it.offset.third },
                EXPRESSION_CODEC.fieldOf("radius").forGetter { it.radius },
                PrimitiveCodec.BOOL.fieldOf("surfaceOnly").forGetter { it.surfaceOnly }
            ).apply(instance) { _, offsetX, offsetY, offsetZ, radius, surfaceOnly ->
                SphereParticleEmitterShape(Triple(offsetX, offsetY, offsetZ), radius, surfaceOnly)
            }
        }
    }

    override val type = ParticleEmitterShapeType.SPHERE

    override fun <T> encode(ops: DynamicOps<T>): DataResult<T> {
        return CODEC.encodeStart(ops, this)
    }

    override fun readFromBuffer(buffer: PacketByteBuf) {
        offset = Triple(
            MoLang.createParser(buffer.readString()).parseExpression(),
            MoLang.createParser(buffer.readString()).parseExpression(),
            MoLang.createParser(buffer.readString()).parseExpression()
        )
        radius = MoLang.createParser(buffer.readString()).parseExpression()
        surfaceOnly = buffer.readBoolean()
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(offset.first.attributes["string"] as String)
        buffer.writeString(offset.second.attributes["string"] as String)
        buffer.writeString(offset.third.attributes["string"] as String)
        buffer.writeString(radius.attributes["string"] as String)
        buffer.writeBoolean(surfaceOnly)
    }

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