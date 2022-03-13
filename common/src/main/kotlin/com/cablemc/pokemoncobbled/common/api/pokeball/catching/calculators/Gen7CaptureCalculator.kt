package com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CaptureContext
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import kotlin.math.pow
import kotlin.random.Random.Default.nextInt

/**
 * Calculates captures for generation 7 and generation 8.
 * This uses the algorithm from https://bulbapedia.bulbagarden.net/wiki/Catch_rate#Capture_method_.28Generation_VI.29
 *
 * @author landonjw
 * @since  December 10, 2021
 */
class Gen7CaptureCalculator : CaptureCalculator {

    override fun processCapture(player: ServerPlayer, pokemon: Pokemon, pokeBall: PokeBall): CaptureContext {
        val catchRate = getCatchRate(player, pokemon, pokeBall)
        if (tryCriticalCapture(catchRate, player)) {
            return CaptureContext(isSuccessfulCapture = true, isCriticalCapture = true, numberOfShakes = 1)
        }
        else {
            val shakeProbability = (25536 / (255 / catchRate.toDouble()).pow(3.0 / 16))

            var numShakes = 0
            for (i in 0..3) {
                if (nextInt(65536) >= shakeProbability) {
                    break
                }
                numShakes++
            }
            return CaptureContext(isSuccessfulCapture = numShakes == 4, isCriticalCapture = false, numberOfShakes = numShakes)
        }
    }

    fun getCatchRate(player: ServerPlayer, pokemon: Pokemon, pokeBall: PokeBall): Float {
        var catchRate = pokemon.species.catchRate.toFloat()
        pokeBall.catchRateModifiers.forEach { catchRate = it.modifyCatchRate(catchRate, player, pokemon) }
        val maxHealth = pokemon.hp
        val currentHealth = pokemon.currentHealth
        val statusBonus = getStatusBonus(pokemon)
        return ((3 * maxHealth - 2 * currentHealth) * catchRate) * statusBonus / (3 * maxHealth)
    }

    private fun tryCriticalCapture(catchRate: Float, player: ServerPlayer): Boolean {
        val critCaptureRate = (minOf(255f, catchRate) * getCriticalCaptureMultiplier(player) / 6).toInt()
        return nextInt(256) < critCaptureRate
    }

    fun getCriticalCaptureMultiplier(player: ServerPlayer): Float {
        // TODO: Get pokedex, determine modifier based on how many pokemon player has caught
        return 0f
    }

    fun getStatusBonus(pokemon: Pokemon): Float {
        // TODO: Get status from pokemon and get bonus (2 for sleep and freeze, 1.5 for paralyze, poison, or burn, and 1 otherwise).
        return 1f
    }

}