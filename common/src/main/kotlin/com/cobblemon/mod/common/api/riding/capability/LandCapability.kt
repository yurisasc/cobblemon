/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.capability

import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.api.tags.CobblemonBlockTags
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.blockPositionsAsList
import com.cobblemon.mod.common.util.blockPositionsAsListRounded
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.FluidTags
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import java.util.function.Predicate

class LandCapability(override val properties: RideControllerProperties) : RidingCapability {

    override val key: Identifier = RidingCapability.LAND
    override val condition: Predicate<PokemonEntity> = Predicate<PokemonEntity> { _ -> false }

}