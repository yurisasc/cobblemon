package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.codec.CodecMapped
import com.cobblemon.mod.common.util.codec.EXPRESSION_CODEC
import com.cobblemon.mod.common.util.resolveDouble
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.ListCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.PacketByteBuf

interface MoLangCurve : CodecMapped {
    companion object : ArbitrarilyMappedSerializableCompanion<MoLangCurve, CurveType>(
        keyFromString = CurveType::valueOf,
        keyFromValue = { it.type },
        stringFromKey = { it.name }
    ) {
//        val CODEC: Codec<ParticleEffectCurve> = RecordCodecBuilder.create { instance ->
//            instance.group(
//                PrimitiveCodec.STRING.fieldOf("type").forGetter { it.type.name },
//
//            ).apply(instance, {
//
//            })
//        }

//        val CURVE_LIST_CODEC: Codec<MutableList<ParticleEffectCurve>>
    }

    var name: String
    val type: CurveType
    var input: Expression
    fun resolve(runtime: MoLangRuntime, inputValue: Double): Expression
    fun apply(runtime: MoLangRuntime) {
        val inputValue = runtime.resolveDouble(input)
        runtime.execute(MoLang.createParser("variable.$name = ${runtime.resolveDouble(resolve(runtime, inputValue))}").parseExpression())
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

    override fun resolve(runtime: MoLangRuntime, inputValue: Double): Expression {
        TODO("Not yet implemented")
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