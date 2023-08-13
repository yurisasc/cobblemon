package com.cobblemon.mod.common.pokemon.riding

import com.cobblemon.mod.common.util.math.pow
import net.minecraft.util.math.Vec3d
import kotlin.math.max
import kotlin.math.min

/**
 * Calculates the riding speed of a pokemon based on 5 given variables. The speed of a pokemon when it comes to riding
 * is based on 5 factors. These factors are as follows: tick, velocity input, speed, acceleration, and weight.
 *
 * Tick: This represents the current tick since the start of the movement input, where the input has been non-zero.
 * This is effectively meant to be used for time when calculating acceleration.
 *
 * Input: This represents the movement input specified by the riding entity. This can be used to help model the
 * velocity the player is expecting the pokemon to travel in, and can be used to apply different instructions
 * based on the direction of the input
 *
 * Speed: This is a constant value typically between 1 and 5, where 3 acts as the median speed. These factors control
 * the max speed allowed for a pokemon. This differs from the pokemon's own speed stat, and is meant to better reflect
 * their pokedex entries.
 *
 * Acceleration: This is a constant value typically between 1 and 5, where 3 acts as the median acceleration. These
 * factors control the rate at which a pokemon can get to their max speed from the start of movement input.
 *
 * Weight: This is a constant variable which describes the pokemon's weight. This can directly affect the acceleration
 * capabilities, to allow for greater variance in the resulting time it takes to reach max speed, as defined by the
 * speed variable.
 *
 * @since 1.6.0
 */
object SpeedCalculator {

    private const val min = 0.5F
    private const val max = 2.5F

    fun calculate(tick: Int, input: Vec3d, speed: Float, acceleration: Float, weight: Float) : Float {
        val time = min(tick, 100)
        val s = ((speed.coerceIn(1.0F, 5.0F) / 5) * this.max).coerceIn(this.min, this.max)
        val a = (((1 - (weight / 500)) * 4) + 1) * (acceleration.coerceIn(1.0F, 5.0F) / 5)

        println("Time: $time, Speed: $s, Acceleration: $a, Momentum: ${((a * time) * (time / 1000.0F)).coerceAtMost(s)}")
        return ((a * time) * (time / 1000.0F)).coerceAtMost(s)
    }

}