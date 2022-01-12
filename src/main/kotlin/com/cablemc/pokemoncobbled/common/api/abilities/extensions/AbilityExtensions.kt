package com.cablemc.pokemoncobbled.common.api.abilities.extensions

import com.cablemc.pokemoncobbled.common.api.abilities.Ability

/**
 * Registry for all known extended Abilities
 *
 * You need to register your extended Ability here or else it doesn't get loaded.
 */
object AbilityExtensions {

    private val extensionMap = mutableMapOf<String, Class<out Ability>>()

    // Extensions - START
    init {
        add("flash_fire", Test::class.java)
    }
    // Extensions - END

    fun get(ability: String) = extensionMap[ability]

    fun add(ability: String, clazz: Class<out Ability>) {
        extensionMap[ability] = clazz
    }

    fun remove(ability: String) = extensionMap.remove(ability)

    fun contains(ability: String) = extensionMap.containsKey(ability)

    fun count() = extensionMap.count()

}