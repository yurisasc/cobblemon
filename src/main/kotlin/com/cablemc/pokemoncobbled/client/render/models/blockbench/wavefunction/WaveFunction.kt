package com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction

import net.minecraft.util.Mth.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

typealias WaveFunction = (Float) -> Float
typealias CoFunction = (Float) -> Pair<Float, Float>
typealias TriFunction = (Float) -> Triple<Float, Float, Float>

fun gradient(function: WaveFunction, t0: Float, t1: Float) = (function(t1) - function(t0)) / (t1 - t0)

operator fun WaveFunction.plus(other: WaveFunction): WaveFunction = { t -> this(t) + other(t) }
operator fun WaveFunction.times(other: WaveFunction): WaveFunction = { t -> this(t) * other(t) }
fun WaveFunction.aggregate(func: WaveFunction): WaveFunction = { t -> func(this(t)) }
fun sineFunction(amplitude: Float = 1F, period: Float = 1F, phaseShift: Float = 0F, verticalShift: Float = 0F): WaveFunction = { t -> sin(2*PI/period * (t - phaseShift)) * amplitude  + verticalShift }
fun cosineFunction(amplitude: Float = 1F, period: Float = 1F, phaseShift: Float = 0F, verticalShift: Float = 0F): WaveFunction = { t -> cos(2*PI/period * (t - phaseShift)) * amplitude + verticalShift }
fun triangleFunction(amplitude: Float = 1F, period: Float = 1F, phaseShift: Float = 0F, verticalShift: Float = 0F): WaveFunction = { t ->
    val timeTerm = ((t + 3 * period / 4 - phaseShift) % period) - period/2
    val value = 4 * amplitude / period * abs(timeTerm) - amplitude + verticalShift
    value
}


