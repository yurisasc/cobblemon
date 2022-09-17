/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.pokeball.catching.modifiers

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CatchRateModifier
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity
import java.util.function.Predicate

class MultiplierModifier(private val multiplier: Float, private val condition: Predicate<Pokemon>?) : CatchRateModifier {
    override fun modifyCatchRate(currentCatchRate: Float, thrower: LivingEntity, pokemon: Pokemon, host: Pokemon?): Float {
        return if(condition?.test(pokemon) != false) {
            currentCatchRate * multiplier
        } else {
            currentCatchRate
        }
    }
}