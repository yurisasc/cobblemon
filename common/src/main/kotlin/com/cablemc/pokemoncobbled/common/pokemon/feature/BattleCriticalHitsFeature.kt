package com.cablemc.pokemoncobbled.common.pokemon.feature

/**
 * A feature that keeps track of critical hits during battles.
 * They reset every battle.
 *
 * @author Licious
 * @since October 2nd, 2022
 */
class BattleCriticalHitsFeature : ResettableAmountFeature() {

    override fun createInstance(value: Int) = BattleCriticalHitsFeature().apply { currentValue = value }

    override val name: String = ID

    companion object {
        const val ID = "battle_critical_hits"
    }

}