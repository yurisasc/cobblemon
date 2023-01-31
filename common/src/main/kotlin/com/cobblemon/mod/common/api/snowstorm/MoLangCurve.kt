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
import com.bedrockk.molang.runtime.struct.VariableStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.cobblemon.mod.common.api.codec.CodecMapped
import com.cobblemon.mod.common.util.codec.EXPRESSION_CODEC
import com.cobblemon.mod.common.util.getString
import com.cobblemon.mod.common.util.math.CatmullRomCurve
import com.cobblemon.mod.common.util.math.CubedBezierCurve
import com.cobblemon.mod.common.util.resolve
import com.cobblemon.mod.common.util.resolveDouble
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.ListCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import kotlin.math.floor
import net.minecraft.network.PacketByteBuf

interface MoLangCurve : CodecMapped {
    companion object : ArbitrarilyMappedSerializableCompanion<MoLangCurve, CurveType>(
        keyFromString = CurveType::valueOf,
        keyFromValue = { it.type },
        stringFromKey = { it.name }
    ) {
        init {
            registerSubtype(CurveType.LINEAR, LinearMoLangCurve::class.java, LinearMoLangCurve.CODEC)
            registerSubtype(CurveType.CATMULL_ROM, CatmullRomMoLangCurve::class.java, CatmullRomMoLangCurve.CODEC)
            registerSubtype(CurveType.BEZIER, BezierMoLangCurve::class.java, BezierMoLangCurve.CODEC)
        }
    }

    var name: String
    val type: CurveType
    var input: Expression
    fun resolve(runtime: MoLangRuntime, inputValue: Double): Double
    fun apply(runtime: MoLangRuntime) {
        val inputValue = runtime.resolveDouble(input)
        runtime.environment.setSimpleVariable(name, DoubleValue(inputValue))
    }
}

class LinearMoLangCurve(
    override var name: String,
    override var input: Expression,
    var horizontalRange: Expression,
    var nodes: List<Double>
) : MoLangCurve {
    companion object {
        val CODEC: Codec<LinearMoLangCurve> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                PrimitiveCodec.STRING.fieldOf("name").forGetter { it.name },
                EXPRESSION_CODEC.fieldOf("input").forGetter { it.input },
                EXPRESSION_CODEC.fieldOf("horizontalRange").forGetter { it.horizontalRange },
                ListCodec(PrimitiveCodec.DOUBLE).fieldOf("nodes").forGetter { it.nodes }
            ).apply(instance) { _, name, input, horizontalRange, nodes ->
                LinearMoLangCurve(name, input, horizontalRange, nodes)
            }
        }
    }

    override val type: CurveType = CurveType.LINEAR
    override fun resolve(runtime: MoLangRuntime, inputValue: Double): Double {
        val range = runtime.resolveDouble(horizontalRange)
        val spaceBetweenNodes = range / (nodes.size - 1)
        val rangeIndex = floor(inputValue / spaceBetweenNodes).toInt()
        if (rangeIndex < 0) {
            return nodes[0]
        } else if (rangeIndex + 1 >= nodes.size) {
            return nodes.last()
        } else {
            val leftNode = nodes[rangeIndex]
            val rightNode = nodes[rangeIndex + 1]
            val t = inputValue.mod(spaceBetweenNodes) / spaceBetweenNodes
            return leftNode + (rightNode - leftNode) * t
        }
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {
        name = buffer.readString()
        input = MoLang.createParser(buffer.readString()).parseExpression()
        horizontalRange = MoLang.createParser(buffer.readString()).parseExpression()
        nodes = buffer.readList { buffer.readDouble() }
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(name)
        buffer.writeString(input.getString())
        buffer.writeString(horizontalRange.getString())
        buffer.writeCollection(nodes) { pb, node -> pb.writeDouble(node) }
    }
}

class CatmullRomMoLangCurve(
    override var name: String = "variable",
    override var input: Expression = NumberExpression(0.5),
    var horizontalRange: Expression,
    var nodes: List<Double>
) : MoLangCurve {

    var curve: CatmullRomCurve

    init {
        curve = CatmullRomCurve(nodes)
    }

    companion object {
        val CODEC: Codec<CatmullRomMoLangCurve> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                PrimitiveCodec.STRING.fieldOf("name").forGetter { it.name },
                EXPRESSION_CODEC.fieldOf("input").forGetter { it.input },
                EXPRESSION_CODEC.fieldOf("horizontalRange").forGetter { it.horizontalRange },
                ListCodec(PrimitiveCodec.DOUBLE).fieldOf("nodes").forGetter { it.nodes }
            ).apply(instance) { _, name, input, horizontalRange, nodes -> CatmullRomMoLangCurve(name, input, horizontalRange, nodes) }
        }
    }

    override val type = CurveType.CATMULL_ROM
    override fun resolve(runtime: MoLangRuntime, inputValue: Double): Double {

        val horizontalRange = runtime.resolveDouble(horizontalRange)
        val segments = nodes.size - 3
        val position = (inputValue / horizontalRange) * segments
        val pso = (position + 1) / (segments + 2)

        return curve.getY(pso)

//        val nodesKeyedByTime = nodes.mapIndexed { index, value -> index to value }.toMap()

//        val nodesBelow = nodesKeyedByTime.filterKeys { it <= inputValue }.entries.sortedBy { it.key }
//        val nodesAfter = nodesKeyedByTime.filterKeys { it >= inputValue }.entries.sortedBy { it.key }
//        val spaceBetweenNodes = horizontalRange / (nodes.size - 1)
//
//        if (nodesBelow.isEmpty()) {
//            return nodes[0]
//        } else if (nodesAfter.isEmpty()) {
//            return nodes[nodes.size - 1]
//        } else {
//
//            val points = nodes.mapIndexed { index, y ->
//                val t = index * spaceBetweenNodes
//                Vec2f(t.toFloat(), y.toFloat())
//            }
//
//            val p = (points.size - 1) * inputValue
//            val intPoint = floor(p).toInt()
//
//            val weight = p - intPoint



//            val preBefore = if (nodesBelow.size > 1) nodesBelow[nodesBelow.size - 2] else null
//            val before = nodesBelow.last()
//            val after = nodesAfter.first()
//            val postAfter = if (nodesAfter.size > 1) nodesAfter[1] else null
//
//            val alpha = MathHelper.lerp(inputValue, (before.key - 1) * spaceBetweenNodes, (after.key - 1) * spaceBetweenNodes)
//
//            // Get the nearby nodes, might be 2, might be 3 or 4
//            val nearbyNodes = mutableListOf(before, after)
//            if (preBefore != null) {
//                nearbyNodes.add(0, preBefore)
//            }
//            if (postAfter != null) {
//                nearbyNodes.add(postAfter)
//            }
//
//            val i = (inputValue - before) / (after - before) + if (preBefore != null) 1 else 0
//            val intPoint = floor(i).toInt()
//            val weight = i - intPoint
//
//            val p0 = nearbyNodes[if (intPoint == 0) 0 else intPoint - 1]
//            val p1 = nearbyNodes[intPoint]
//            val p2 = nearbyNodes[if (intPoint > nearbyNodes.size - 2) nearbyNodes.size - 1 else intPoint + 1]
//            val p3 = nearbyNodes[if (intPoint > nearbyNodes.size - 3) nearbyNodes.size - 1 else intPoint + 2]
//
//            val v0 = (p2 - p0) * 0.5
//            val v1 = (p3 - p1) * 0.5
//
//            val weightSquared = weight * weight
//            val weightCubed = weight * weightSquared
//            return (2 * p1 - 2 * p2 + v0 + v1) * weightCubed + (-3 * p1 + 3 * p2 - 2 * v0 - v1) * weightSquared + v0 * weight + p1
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {
        name = buffer.readString()
        input = MoLang.createParser(buffer.readString()).parseExpression()
        horizontalRange = MoLang.createParser(buffer.readString()).parseExpression()
        nodes = buffer.readList { buffer.readDouble() }

        curve = CatmullRomCurve(nodes)
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(name)
        buffer.writeString(input.getString())
        buffer.writeString(horizontalRange.getString())
        buffer.writeCollection(nodes) { pb, node -> pb.writeDouble(node) }
    }
}

class BezierMoLangCurve(
    override var name: String = "variable",
    override var input: Expression = NumberExpression(0.5),
    var horizontalRange: Expression,
    var v0: Double,
    var v1: Double,
    var v2: Double,
    var v3: Double
) : MoLangCurve {
    var curve: CubedBezierCurve

    init {
        curve = CubedBezierCurve(v0, v1, v2, v3)
    }

    companion object {
        val CODEC: Codec<BezierMoLangCurve> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
                PrimitiveCodec.STRING.fieldOf("name").forGetter { it.name },
                EXPRESSION_CODEC.fieldOf("input").forGetter { it.input },
                EXPRESSION_CODEC.fieldOf("horizontalRange").forGetter { it.horizontalRange },
                PrimitiveCodec.DOUBLE.fieldOf("v0").forGetter { it.v0 },
                PrimitiveCodec.DOUBLE.fieldOf("v1").forGetter { it.v1 },
                PrimitiveCodec.DOUBLE.fieldOf("v2").forGetter { it.v2 },
                PrimitiveCodec.DOUBLE.fieldOf("v3").forGetter { it.v3 }
            ).apply(instance) { _, name, input, horizontalRange, v0, v1, v2, v3 -> BezierMoLangCurve(name, input, horizontalRange, v0, v1, v2, v3) }
        }
    }

    override val type = CurveType.BEZIER
    override fun resolve(runtime: MoLangRuntime, inputValue: Double): Double {
        val horizontalRange = runtime.resolveDouble(horizontalRange)
        val position = inputValue / horizontalRange
        return curve.getY(position)
    }

    override fun <T> encode(ops: DynamicOps<T>) = CODEC.encodeStart(ops, this)
    override fun readFromBuffer(buffer: PacketByteBuf) {
        name = buffer.readString()
        input = MoLang.createParser(buffer.readString()).parseExpression()
        horizontalRange = MoLang.createParser(buffer.readString()).parseExpression()
        v0 = buffer.readDouble()
        v1 = buffer.readDouble()
        v2 = buffer.readDouble()
        v3 = buffer.readDouble()
        curve = CubedBezierCurve(v0, v1, v2, v3)
    }

    override fun writeToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(name)
        buffer.writeString(input.getString())
        buffer.writeString(horizontalRange.getString())
        buffer.writeDouble(v0)
        buffer.writeDouble(v1)
        buffer.writeDouble(v2)
        buffer.writeDouble(v3)
    }
}