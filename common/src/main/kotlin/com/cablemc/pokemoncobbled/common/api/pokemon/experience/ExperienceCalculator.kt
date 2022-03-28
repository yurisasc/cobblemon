package com.cablemc.pokemoncobbled.common.api.pokemon.experience

import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import kotlin.math.pow

interface ExperienceCalculator {
    fun calculate(battlePokemon: BattlePokemon) = battlePokemon.facedOpponents /* Change later to be all opponents if exp share held */
        .filter { it.health == 0 }
        .sumOf { calculate(battlePokemon, it) }

    fun calculate(battlePokemon: BattlePokemon, opponentPokemon: BattlePokemon): Int
}

object StandardExperienceCalculator : ExperienceCalculator {
    override fun calculate(battlePokemon: BattlePokemon, opponentPokemon: BattlePokemon): Int {
        val trainerMultiplier = if (opponentPokemon.effectedPokemon.isWild()) 1.0 else 1.5 /* Not used in modern formulas */
        val baseExp = opponentPokemon.originalPokemon.form.baseExperienceYield
        val luckyEggMultiplier = 1.0
        val affectionMultiplier = 1.0
        val level = opponentPokemon.originalPokemon.level
        val levelVictor = battlePokemon.originalPokemon.level
        val pointPowerMultiplier = 1.0
        val participatedMultiplier = if (opponentPokemon in battlePokemon.facedOpponents) 1.0 else 2.0 // Implement after Exp Share etc
        val originalTrainerMultiplier = 1.0
        val evolutionMultiplier = 1.0 // implement after evolutions, it is 'v' on bulbapedia's equation

        val term1 = (baseExp * level * affectionMultiplier * evolutionMultiplier) / (5 * participatedMultiplier)
        val term2 = ((2.0 * level + 10) / (level + levelVictor + 10)).pow(2.5)
        val term3 = originalTrainerMultiplier * luckyEggMultiplier * pointPowerMultiplier

        return (term1 * term2 * term3).toInt()
    }
}