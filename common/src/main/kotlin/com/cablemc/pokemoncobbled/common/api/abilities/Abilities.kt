package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.api.abilities.AbilitiesLoader.loadFromAssets

/**
 * Registry for all known Abilities
 */
object Abilities {
    private val allAbilities = mutableListOf<AbilityTemplate>()

    // Abilities - START
    val FLASH_FIRE = register(loadFromAssets("flash_fire"))
    val DROUGHT = register(loadFromAssets("drought"))
    // Abilities - END

    fun register(ability: AbilityTemplate): AbilityTemplate {
        return ability.also {
            allAbilities.add(it)
        }
    }

    fun get(name: String): AbilityTemplate? {
        return allAbilities.firstOrNull { ability -> ability.name.equals(name, ignoreCase = true) }
    }

    fun getOrException(name: String): AbilityTemplate {
        return allAbilities.first { ability -> ability.name.equals(name, ignoreCase = true) }
    }

    fun count() = allAbilities.size
}