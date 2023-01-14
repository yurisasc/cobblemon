package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.bedrockk.molang.ast.BooleanExpression
import com.bedrockk.molang.ast.NumberExpression
import net.minecraft.util.Identifier

class BedrockParticle {
    val texture = Identifier("minecraft:fire")
    val material = ParticleMaterial.ALPHA
    val uvMode: ParticleUVMode = StaticParticleUVMode()
    val sizeX: Expression = NumberExpression(0.15)
    val sizeY: Expression = NumberExpression(0.15)
    val maxAge: Expression = NumberExpression(1.0)
    val killExpression: Expression = BooleanExpression(false)
    val updateExpressions = mutableListOf<Expression>()
    val renderExpressions = mutableListOf<Expression>()
    val motion: ParticleMotion = StaticParticleMotion()
    // kill plane maybe
}