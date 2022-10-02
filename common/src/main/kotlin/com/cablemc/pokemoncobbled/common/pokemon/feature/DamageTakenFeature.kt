package com.cablemc.pokemoncobbled.common.pokemon.feature

/**
 * A feature that keeps track of battle damage.
 * They reset when a Pokémon is healed or faints.
 *
 * @author Licious
 * @since October 2nd, 2022
 */
class DamageTakenFeature : ResettableAmountFeature() {

    override fun createInstance(value: Int) = DamageTakenFeature().apply { currentValue = value }

    override val name: String = ID

    companion object {
        const val ID = "damage_taken"
    }

}