package com.cablemc.pokemoncobbled.common.util.math.geometry

private const val RADIAN_IN_DEGREES = 57.2958f

fun Number.toRadians(): Float = this.toFloat() / RADIAN_IN_DEGREES
fun Number.toDegrees(): Float = this.toFloat() * RADIAN_IN_DEGREES