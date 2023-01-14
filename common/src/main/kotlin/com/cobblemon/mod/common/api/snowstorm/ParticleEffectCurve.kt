package com.cobblemon.mod.common.api.snowstorm

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.api.serialization.ClassMapAdapter
import com.cobblemon.mod.common.util.getFromJSON

interface ParticleEffectCurve {
    companion object {
        val curves = mutableMapOf<CurveType, Class<out ParticleEffectCurve>>()
        val adapter = ClassMapAdapter(curves) { CurveType.values().getFromJSON(it, "type") }
    }

    val type: CurveType
    val nodes: List<Float>
    fun resolve(): Expression
}

