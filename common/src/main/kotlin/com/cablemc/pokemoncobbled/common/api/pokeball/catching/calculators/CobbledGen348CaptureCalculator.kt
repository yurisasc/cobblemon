package com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CaptureContext
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

object CobbledGen348CaptureCalculator : CaptureCalculator {

    override fun processCapture(thrower: LivingEntity, pokeBall: PokeBall, target: Pokemon, host: Pokemon?): CaptureContext {
        val catchRate = getCatchRate(thrower, pokeBall, target, host)
        return if (tryCriticalCapture(catchRate, thrower)) {
            CaptureContext(isSuccessfulCapture = true, isCriticalCapture = true, numberOfShakes = 1)
        } else {
            val shakeProbability = (65536 / (255 / catchRate.toDouble()).pow(3.0 / 16))

            var numShakes = 0
            for (i in 0..3) {
                if (Random.nextInt(65536) >= shakeProbability) {
                    break
                }
                numShakes++
            }

            CaptureContext(isSuccessfulCapture = numShakes == 4, isCriticalCapture = false, numberOfShakes = numShakes)
        }
    }

    private fun getCatchRate(thrower: LivingEntity, pokeBall: PokeBall, target: Pokemon, host: Pokemon?): Float {
        var catchRate = target.species.catchRate.toFloat()
        pokeBall.catchRateModifiers.forEach { catchRate = it.modifyCatchRate(catchRate, thrower, target, host) }

        val guaranteed = pokeBall.catchRateModifiers.any { it.isGuaranteed() }
        val statusBonus = this.getStatusBonus(target)

        val base = (((3 * target.hp - 2 * target.currentHealth) * catchRate) / (3 * target.hp)) * statusBonus
        val l = max(1, (30 - target.level) / 10)
        val d = min(410.0f, max(1.0f, (host?.level ?: 10) / (10.0f / (100 / target.level))) * 410.0f) / 4096.0f

        return if(guaranteed) 255.0f else (base * l * d)
    }

    private fun tryCriticalCapture(catchRate: Float, thrower: LivingEntity): Boolean {
        val critCaptureRate = (minOf(255f, catchRate) * getCriticalCaptureMultiplier(thrower) / 6).toInt()
        return Random.nextInt(256) < critCaptureRate
    }

    private fun getCriticalCaptureMultiplier(thrower: LivingEntity): Float {
        // TODO: Get pokedex, determine modifier based on how many pokemon player has caught
        return 0f
    }

    private fun getStatusBonus(pokemon: Pokemon): Float {
        // TODO: Get status from pokemon and get bonus (2 for sleep and freeze, 1.5 for paralyze, poison, or burn, and 1 otherwise).
        return 1f
    }

}