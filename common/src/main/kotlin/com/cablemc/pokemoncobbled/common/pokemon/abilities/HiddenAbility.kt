package com.cablemc.pokemoncobbled.common.pokemon.abilities

import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.cablemc.pokemoncobbled.common.api.abilities.CommonAbility
import com.cablemc.pokemoncobbled.common.api.abilities.PotentialAbility
import com.google.gson.JsonElement

/**
 * Crappy Pokémon feature
 *
 * @author Hiroku
 * @since July 28th, 2022
 */
class HiddenAbility(override val template: AbilityTemplate) : PotentialAbility {
    override val priority: Priority = Priority.LOW
    override fun isSatisfiedBy(aspects: Set<String>) = false // TODO actually implement hidden abilities ig? Chance in config or aspect check?
    companion object {
        val interpreter: (JsonElement) -> PotentialAbility? = {
            val str = if (it.isJsonPrimitive) it.asString else null
            if (str?.startsWith("h:") == true) {
                val ability = Abilities.get(str.substringAfter("h:"))
                if (ability != null) {
                    CommonAbility(ability)
                } else {
                    null
                }
            } else {
               null
            }
        }
    }
}