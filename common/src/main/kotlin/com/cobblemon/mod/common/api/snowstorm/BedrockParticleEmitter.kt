package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder

class BedrockParticleEmitter(
    var updateExpressions: MutableList<Expression> = mutableListOf(),
    var rate: ParticleEmitterRate = InstantParticleEmitterRate(),
    var shape: ParticleEmitterShape = SphereParticleEmitterShape()
) {
    companion object {
        val CODEC: Codec<BedrockParticleEmitter> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING
                    .fieldOf("updateExpressions")
                    .forGetter { it.updateExpressions.joinToString(separator = "\n") { it.attributes["string"] as String } },
                ParticleEmitterShape.codec.fieldOf("shape").forGetter { it.shape }
            ).apply(instance) { expressions, /*rate,*/ shape ->
                BedrockParticleEmitter(
                    updateExpressions = expressions.split("\n").map { MoLang.createParser(it).parseExpression() }.toMutableList(),
                    shape = shape
                )
            }
        }
    }

}