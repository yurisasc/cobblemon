/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.bedrockk.molang.ast.NumberExpression
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.codec.CodecMapped
import com.cobblemon.mod.common.util.codec.EXPRESSION_CODEC
import com.cobblemon.mod.common.util.getString
import com.cobblemon.mod.common.util.resolveDouble
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.lang.Integer.min
import kotlin.random.Random
import net.minecraft.network.PacketByteBuf

interface ParticleEmitterRate : CodecMapped {
    companion object : ArbitrarilyMappedSerializableCompanion<ParticleEmitterRate, ParticleEmitterRateType>(
        keyFromValue = { it.type },
        keyFromString = ParticleEmitterRateType::valueOf,
        stringFromKey = { it.name }
    ) {
        init {
            registerSubtype(ParticleEmitterRateType.INSTANT, InstantParticleEmitterRate::class.java, InstantParticleEmitterRate.CODEC)
            registerSubtype(ParticleEmitterRateType.STEADY, SteadyParticleEmitterRate::class.java, SteadyParticleEmitterRate.CODEC)
        }
    }

    val type: ParticleEmitterRateType
    fun getEmitCount(runtime: MoLangRuntime, currentlyActive: Int, deltaTicks: Float): Int
}

enum class ParticleEmitterRateType {
    STEADY,
    INSTANT
}

class InstantParticleEmitterRate(var amount: Int = 1) : ParticleEmitterRate {
    companion object {
        val CODEC: Codec<InstantParticleEmitterRate> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                PrimitiveCodec.INT.fieldOf("amount").forGetter { it.amount }
            ).apply(instance) { _, amount -> InstantParticleEmitterRate(amount) }
        }
    }

    override val type = ParticleEmitterRateType.INSTANT

    override fun getEmitCount(runtime: MoLangRuntime, currentlyActive: Int, deltaTicks: Float): Int {
        if (currentlyActive > 0) {
            return 0
        } else {
            return amount
        }
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {
        amount = buffer.readInt()
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        buffer.writeInt(amount)
    }
}

class SteadyParticleEmitterRate(
    var rate: Expression = NumberExpression(0.0),
    var maximum: Expression = NumberExpression(0.0)
) : ParticleEmitterRate {
    companion object {
        val CODEC: Codec<SteadyParticleEmitterRate> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                EXPRESSION_CODEC.fieldOf("rate").forGetter { it.rate },
                EXPRESSION_CODEC.fieldOf("maximum").forGetter { it.maximum }
            ).apply(instance) { _, rate, maximum -> SteadyParticleEmitterRate(rate, maximum) }
        }
    }

    override val type = ParticleEmitterRateType.STEADY

    override fun getEmitCount(runtime: MoLangRuntime, currentlyActive: Int, deltaSeconds: Float): Int {
        val max = runtime.resolveDouble(maximum).toInt()
        if (currentlyActive >= max) {
            return 0
        }

        val perSecond = runtime.resolveDouble(rate)
        val trySpawn = perSecond * deltaSeconds
        var intComponent = trySpawn.toInt()
        val doubleComponent = trySpawn - intComponent
        if (doubleComponent > 0.0) {
            if ((1 - Random.Default.nextDouble()) <= doubleComponent) {
                intComponent++
            }
        }
        return min(intComponent, max - currentlyActive)
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {
        rate = MoLang.createParser(buffer.readString()).parseExpression()
        maximum = MoLang.createParser(buffer.readString()).parseExpression()
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(rate.getString())
        buffer.writeString(maximum.getString())
    }
}