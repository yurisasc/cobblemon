/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CaptureContext
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity

object DebugCaptureCalculator : CaptureCalculator {
    override fun processCapture(thrower: LivingEntity, pokeBall: PokeBall, target: Pokemon, host: Pokemon?): CaptureContext {
        return CaptureContext(numberOfShakes = 4, isSuccessfulCapture = true, isCriticalCapture = false)
    }
}