/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.capability

import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import java.util.function.Predicate

class LandCapability(override val controller: RideControllerProperties) : RidingCapability {

    override val key: Identifier = RidingCapability.LAND
    override val condition: Predicate<PokemonEntity> = Predicate<PokemonEntity> {
        it.blockStateAtPos.isSolidSurface(it.world, it.blockPos, it, Direction.DOWN)
    }

}