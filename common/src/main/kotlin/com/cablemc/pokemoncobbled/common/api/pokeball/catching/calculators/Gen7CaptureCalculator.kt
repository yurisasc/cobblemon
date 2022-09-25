/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CaptureContext
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import kotlin.math.pow
import kotlin.random.Random.Default.nextInt
import net.minecraft.entity.LivingEntity

/**
 * Calculates captures for generation 7 and generation 8.
 * This uses the algorithm from https://bulbapedia.bulbagarden.net/wiki/Catch_rate#Capture_method_.28Generation_VI.29
 *
 * @author landonjw
 * @since  December 10, 2021
 */
object Gen7CaptureCalculator : CaptureCalculator {

    override fun processCapture(thrower: LivingEntity, pokeBall: PokeBall, target: Pokemon, host: Pokemon?): CaptureContext {
        val catchRate = getCatchRate(thrower, pokeBall, target, host)
        return if (tryCriticalCapture(catchRate, thrower)) {
            CaptureContext(isSuccessfulCapture = true, isCriticalCapture = true, numberOfShakes = 1)
        } else {
            val shakeProbability = (65536 / (255 / catchRate.toDouble()).pow(3.0 / 16))

            var numShakes = 0
            for (i in 0..3) {
                if (nextInt(65536) >= shakeProbability) {
                    break
                }
                numShakes++
            }

            CaptureContext(isSuccessfulCapture = numShakes == 4, isCriticalCapture = false, numberOfShakes = numShakes)
        }
    }

    fun getCatchRate(thrower: LivingEntity, pokeBall: PokeBall, target: Pokemon, host: Pokemon?): Float {
        var catchRate = target.species.catchRate.toFloat()
        pokeBall.catchRateModifiers.forEach { catchRate = it.modifyCatchRate(catchRate, thrower, target, host) }
        val maxHealth = target.hp
        val currentHealth = target.currentHealth
        val statusBonus = getStatusBonus(target)

        return (((3 * maxHealth - 2 * currentHealth) * catchRate) / (3 * maxHealth)) * statusBonus
    }

    private fun tryCriticalCapture(catchRate: Float, thrower: LivingEntity): Boolean {
        val critCaptureRate = (minOf(255f, catchRate) * getCriticalCaptureMultiplier(thrower) / 6).toInt()
        return nextInt(256) < critCaptureRate
    }

    fun getCriticalCaptureMultiplier(thrower: LivingEntity): Float {
        // TODO: Get pokedex, determine modifier based on how many pokemon player has caught
        return 0f
    }

    fun getStatusBonus(pokemon: Pokemon): Float {
        // TODO: Get status from pokemon and get bonus (2 for sleep and freeze, 1.5 for paralyze, poison, or burn, and 1 otherwise).
        return 1f
    }
}