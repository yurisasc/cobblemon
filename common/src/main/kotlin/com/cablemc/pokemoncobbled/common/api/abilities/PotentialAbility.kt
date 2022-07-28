package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.api.Priority
import com.google.gson.JsonElement

/**
 * An ability on a species that may or may not be available for a specific instance of the species.
 *
 * Controls whether a Pokémon can learn an ability.
 *
 * @author Hiroku
 * @since July 27th, 2022
 */
interface PotentialAbility {
    val template: AbilityTemplate
    val priority: Priority
    fun isSatisfiedBy(aspects: Set<String>): Boolean
    companion object {
        val interpreters = mutableListOf<(JsonElement) -> PotentialAbility?>()
    }
}

open class CommonAbility(override val template: AbilityTemplate) : PotentialAbility {
    override val priority = Priority.LOWEST
    override fun isSatisfiedBy(aspects: Set<String>) = true
    companion object {
        val interpreter: (JsonElement) -> PotentialAbility? = {
            val str = if (it.isJsonPrimitive) it.asString else null
            str?.let {
                val ability = Abilities.get(it)
                if (ability != null) {
                    return@let CommonAbility(ability)
                } else {
                    return@let null
                }
            }
        }
    }
}

