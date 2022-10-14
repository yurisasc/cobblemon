/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokeball.catching.modifiers

import com.cablemc.pokemod.common.api.pokeball.catching.CatchRateModifier
import com.cablemc.pokemod.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity

class GuaranteedModifier : CatchRateModifier {
    override fun modifyCatchRate(currentCatchRate: Float, thrower: LivingEntity, pokemon: Pokemon, host: Pokemon?): Float {
        return 255.0f // Catch rates should check the CatchRateModifier#isGuaranteed call
    }
}