/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.entity.LivingEntity

class WeatherRequirement : EntityQueryRequirement {
    companion object {
        const val ADAPTER_VARIANT = "weather"
    }
    val isRaining: Boolean? = null
    val isThundering: Boolean? = null

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        val world = queriedEntity.world
        return when {
            isRaining == true && !world.isRaining -> false
            isRaining == false && world.isRaining -> false
            isThundering == true && !world.isThundering -> false
            isThundering == false && world.isThundering -> false
            else -> true
        }
    }
}