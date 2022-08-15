package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class AttackDefenceRatioRequirement : EvolutionRequirement {
    companion object {
        const val ADAPTER_VARIANT = "attack_defence_ratio"
    }
    enum class AttackDefenceRatio {
        ATTACK_HIGHER,
        DEFENCE_HIGHER,
        EQUAL
    }

    val ratio = AttackDefenceRatio.ATTACK_HIGHER

    override fun check(pokemon: Pokemon): Boolean {
        return when (ratio) {
            AttackDefenceRatio.ATTACK_HIGHER -> pokemon.attack > pokemon.defence
            AttackDefenceRatio.DEFENCE_HIGHER -> pokemon.defence > pokemon.attack
            AttackDefenceRatio.EQUAL -> pokemon.attack == pokemon.defence
        }
    }
}