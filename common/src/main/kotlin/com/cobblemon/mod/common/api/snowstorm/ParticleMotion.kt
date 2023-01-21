package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.bedrockk.molang.ast.NumberExpression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.codec.CodecMapped
import com.cobblemon.mod.common.api.serialization.ClassMapAdapter
import com.cobblemon.mod.common.util.codec.EXPRESSION_CODEC
import com.cobblemon.mod.common.util.getFromJSON
import com.cobblemon.mod.common.util.resolveDouble
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import kotlin.reflect.jvm.internal.impl.types.model.DynamicTypeMarker
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

interface ParticleMotion : CodecMapped {
    companion object : ArbitrarilyMappedSerializableCompanion<ParticleMotion, ParticleMotionType>(
        keyFromValue = { it.type },
        keyFromString = ParticleMotionType::valueOf,
        stringFromKey = { it.name }
    ) {
        init {
            registerSubtype(ParticleMotionType.DYNAMIC, DynamicParticleMotion::class.java, DynamicParticleMotion.CODEC)
            registerSubtype(ParticleMotionType.STATIC, StaticParticleMotion::class.java, StaticParticleMotion.CODEC)
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

class DynamicParticleMotion(
    var direction: ParticleMotionDirection = InwardsMotionDirection(),
    var speed: Expression = NumberExpression(0.0),
    var acceleration: Triple<Expression, Expression, Expression> = Triple(
        NumberExpression(0.0),
        NumberExpression(0.0),
        NumberExpression(0.0)
    ),
    var drag: Expression = NumberExpression(0.0)
) : ParticleMotion {
    override val type = ParticleMotionType.DYNAMIC

    companion object {
        val CODEC: Codec<DynamicParticleMotion> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                ParticleMotionDirection.codec.fieldOf("direction").forGetter { it.direction },
                EXPRESSION_CODEC.fieldOf("speed").forGetter { it.speed },
                EXPRESSION_CODEC.fieldOf("accelX").forGetter { it.acceleration.first },
                EXPRESSION_CODEC.fieldOf("accelY").forGetter { it.acceleration.second },
                EXPRESSION_CODEC.fieldOf("accelZ").forGetter { it.acceleration.third },
                EXPRESSION_CODEC.fieldOf("drag").forGetter { it.drag }
            ).apply(instance) { _, direction, speed, accelX, accelY, accelZ, drag -> DynamicParticleMotion(direction, speed, Triple(accelX, accelY, accelZ), drag) }
        }
    }

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

    override fun <T> encode(ops: DynamicOps<T>): DataResult<T> {
        TODO("Not yet implemented")
    }

    override fun readFromBuffer(buffer: PacketByteBuf) {
        TODO("Not yet implemented")
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        TODO("Not yet implemented")
    }
}

interface ParticleMotionDirection : CodecMapped {
    companion object : ArbitrarilyMappedSerializableCompanion<ParticleMotionDirection, ParticleMotionDirectionType>(
        keyFromString = ParticleMotionDirectionType::valueOf,
        stringFromKey = { it.name },
        keyFromValue = { it.type }
    ) {
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
    companion object {
        val CODEC: Codec<InwardsMotionDirection> = RecordCodecBuilder.create { instance ->
            instance.group(PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name })
                .apply(instance) { InwardsMotionDirection() }
        }
    }

    override val type = ParticleMotionDirectionType.INWARDS
    override fun getDirectionVector(runtime: MoLangRuntime, emitterPos: Vec3d, particlePos: Vec3d) = emitterPos.subtract(particlePos).normalize()
    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}

class OutwardsMotionDirection : ParticleMotionDirection {
    companion object {
        val CODEC: Codec<OutwardsMotionDirection> = RecordCodecBuilder.create { instance ->
            instance.group(PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name })
                .apply(instance) { OutwardsMotionDirection() }
        }
    }

    override val type = ParticleMotionDirectionType.OUTWARDS
    override fun getDirectionVector(runtime: MoLangRuntime, emitterPos: Vec3d, particlePos: Vec3d) = particlePos.subtract(emitterPos).normalize()

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}

class CustomMotionDirection(
    var direction: Triple<Expression, Expression, Expression> = Triple(
        NumberExpression(0.0),
        NumberExpression(0.0),
        NumberExpression(0.0)
    )
) : ParticleMotionDirection {
    companion object {
        val CODEC: Codec<CustomMotionDirection> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                EXPRESSION_CODEC.fieldOf("directionX").forGetter { it.direction.first },
                EXPRESSION_CODEC.fieldOf("directionY").forGetter { it.direction.second },
                EXPRESSION_CODEC.fieldOf("directionZ").forGetter { it.direction.third }
            ).apply(instance) { _, dirX, dirY, dirZ -> CustomMotionDirection(Triple(dirX, dirY, dirZ)) }
        }
    }

    override val type = ParticleMotionDirectionType.CUSTOM

    override fun getDirectionVector(runtime: MoLangRuntime, emitterPos: Vec3d, particlePos: Vec3d): Vec3d {
        return Vec3d(
            runtime.resolveDouble(direction.first),
            runtime.resolveDouble(direction.second),
            runtime.resolveDouble(direction.third)
        )
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {
        direction = Triple(
            MoLang.createParser(buffer.readString()).parseExpression(),
            MoLang.createParser(buffer.readString()).parseExpression(),
            MoLang.createParser(buffer.readString()).parseExpression()
        )
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(direction.first.attributes["string"] as String)
        buffer.writeString(direction.second.attributes["string"] as String)
        buffer.writeString(direction.third.attributes["string"] as String)
    }
}

enum class ParticleMotionDirectionType {
    CUSTOM,
    INWARDS,
    OUTWARDS
}

class StaticParticleMotion : ParticleMotion {
    companion object {
        val CODEC: Codec<StaticParticleMotion> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name }
            ).apply(instance) { StaticParticleMotion() }
        }
    }

    @Transient
    override val type = ParticleMotionType.STATIC

    override fun getInitialVelocity(runtime: MoLangRuntime, particlePos: Vec3d, emitterPos: Vec3d) = Vec3d.ZERO
    override fun getAcceleration(runtime: MoLangRuntime, velocity: Vec3d) = Vec3d.ZERO
    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {}
    override fun writeToBuffer(buffer: PacketByteBuf) {}
}