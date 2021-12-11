package com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

typealias LineFunction = (Float) -> Float
typealias CoFunction = (Float) -> Pair<Float, Float>
typealias TriFunction = (Float) -> Triple<Float, Float, Float>

fun gradient(function: LineFunction, t0: Float, t1: Float) = (function(t1) - function(t0)) / (t1 - t0)

operator fun LineFunction.plus(other: LineFunction): LineFunction = { t -> this(t) + other(t) }
operator fun LineFunction.times(other: LineFunction): LineFunction = { t -> this(t) * other(t) }
fun LineFunction.aggregate(func: LineFunction): LineFunction = { t -> func(this(t)) }
fun sineFunction(amplitude: Float = 1F, period: Float = 1F, phaseShift: Float = 0F, verticalShift: Float = 0F): LineFunction = { t -> sin(t/period - phaseShift) * amplitude  + verticalShift }
fun cosineFunction(amplitude: Float = 1F, period: Float = 1F, phaseShift: Float = 0F, verticalShift: Float = 0F): LineFunction = { t -> cos(t/period - phaseShift) * amplitude + verticalShift }
fun triangleFunction(amplitude: Float = 1F, period: Float = 1F, phaseShift: Float = 0F, verticalShift: Float = 0F): LineFunction = { t ->
    val timeTerm = ((t - period/4 - phaseShift) % period) - period/2
    val value = 4 * amplitude / period * abs(timeTerm) - amplitude + verticalShift
    value
}


