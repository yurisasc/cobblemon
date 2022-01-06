package com.cablemc.pokemoncobbled.common.util.math

import net.minecraft.core.Rotations
import net.minecraft.world.phys.Vec3
import kotlin.math.pow

infix fun Int.pow(power: Int): Int {
    return toDouble().pow(power.toDouble()).toInt()
}

fun Vec3.toRotations() = Rotations(x.toFloat(), y.toFloat(), z.toFloat())
fun Rotations.toVec3() = Vec3(x.toDouble(), y.toDouble(), z.toDouble())