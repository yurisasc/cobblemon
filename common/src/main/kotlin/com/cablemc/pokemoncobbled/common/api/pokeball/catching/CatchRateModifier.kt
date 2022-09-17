/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.pokeball.catching

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.modifiers.GuaranteedModifier
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity

/**
 * A modifier that can be used to modify the chance a poke ball.
 * This is often used to add modifiers to [PokeBall] for the different types, ie. ultra ball, great ball, dive ball, etc.
 *
 * @author landonjw
 * @since  November 30, 2021
 */
interface CatchRateModifier {

    fun isGuaranteed() : Boolean {
        return this is GuaranteedModifier
    }

    fun modifyCatchRate(currentCatchRate: Float, thrower: LivingEntity, pokemon: Pokemon, host: Pokemon?): Float
}