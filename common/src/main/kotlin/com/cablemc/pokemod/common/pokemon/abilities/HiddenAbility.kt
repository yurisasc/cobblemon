/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.abilities

import com.cablemc.pokemod.common.api.Priority
import com.cablemc.pokemod.common.api.abilities.Abilities
import com.cablemc.pokemod.common.api.abilities.AbilityTemplate
import com.cablemc.pokemod.common.api.abilities.PotentialAbility
import com.cablemc.pokemod.common.api.abilities.PotentialAbilityType
import com.google.gson.JsonElement

object HiddenAbilityType : PotentialAbilityType<HiddenAbility> {
    override fun parseFromJSON(element: JsonElement): HiddenAbility? {
        val str = if (element.isJsonPrimitive) element.asString else null
        return if (str?.startsWith("h:") == true) {
            val ability = Abilities.get(str.substringAfter("h:"))
            if (ability != null) {
                HiddenAbility(ability)
            } else {
                null
            }
        } else {
            null
        }
    }
}

/**
 * Crappy Pokémon feature
 *
 * @author Hiroku
 * @since July 28th, 2022
 */
class HiddenAbility(override val template: AbilityTemplate) : PotentialAbility {
    override val priority: Priority = Priority.LOW
    override val type = HiddenAbilityType
    override fun isSatisfiedBy(aspects: Set<String>) = false // TODO actually implement hidden abilities ig? Chance in config or aspect check?
}