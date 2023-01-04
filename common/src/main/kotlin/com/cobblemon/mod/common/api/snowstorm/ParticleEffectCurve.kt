package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression

interface ParticleEffectCurve {
    companion object {
        val curves = mutableMapOf<String, ParticleEffectCurve>()


    }

    val type: CurveType
    val nodes: List<Float>
    fun resolve(): Expression
}