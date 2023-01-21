package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.ast.BooleanExpression
import com.bedrockk.molang.ast.NumberExpression
import com.cobblemon.mod.common.util.codec.EXPRESSION_CODEC
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.ListCodec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier

class BedrockParticle(
    var texture: Identifier = Identifier("minecraft:fire"),
    var material: ParticleMaterial = ParticleMaterial.ALPHA,
    var uvMode: ParticleUVMode = StaticParticleUVMode(),
    var sizeX: Expression = NumberExpression(0.15),
    var sizeY: Expression = NumberExpression(0.15),
    var maxAge: Expression = NumberExpression(1.0),
    var killExpression: Expression = BooleanExpression(false),
    var updateExpressions: MutableList<Expression> = mutableListOf(),
    var renderExpressions: MutableList<Expression> = mutableListOf(),
    var motion: ParticleMotion = StaticParticleMotion()
) {
    companion object {
        val CODEC: Codec<BedrockParticle> = RecordCodecBuilder.create { instance ->
            instance.group(
                Identifier.CODEC.fieldOf("texture").forGetter { it.texture },
                PrimitiveCodec.STRING.fieldOf("material").forGetter { it.material.name },
                ParticleUVMode.codec.fieldOf("uvMode").forGetter { it.uvMode },
                EXPRESSION_CODEC.fieldOf("sizeX").forGetter { it.sizeX },
                EXPRESSION_CODEC.fieldOf("sizeY").forGetter { it.sizeY },
                EXPRESSION_CODEC.fieldOf("maxAge").forGetter { it.maxAge },
                EXPRESSION_CODEC.fieldOf("killExpression").forGetter { it.killExpression },
                ListCodec(EXPRESSION_CODEC).fieldOf("updateExpressions").forGetter { it.updateExpressions },
                ListCodec(EXPRESSION_CODEC).fieldOf("renderExpressions").forGetter { it.renderExpressions },
                ParticleMotion.codec.fieldOf("motion").forGetter { it.motion }
            ).apply(instance) { texture, materialStr, uvMode, sizeX, sizeY, maxAge, killExpression, updateExpressions, renderExpressions, motion ->
                BedrockParticle(
                    texture = texture,
                    material = ParticleMaterial.valueOf(materialStr),
                    uvMode = uvMode,
                    sizeX = sizeX,
                    sizeY = sizeY,
                    maxAge = maxAge,
                    killExpression = killExpression,
                    updateExpressions = updateExpressions,
                    renderExpressions = renderExpressions,
                    motion = motion
                )
            }
        }
    }
    // kill plane maybe
}