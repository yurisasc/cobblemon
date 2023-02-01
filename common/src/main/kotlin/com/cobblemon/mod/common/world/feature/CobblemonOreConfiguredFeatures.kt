/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature

import com.cobblemon.mod.common.Cobblemon
import net.minecraft.registry.RegistryKey
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.ConfiguredFeatures

object CobblemonOreConfiguredFeatures {

    val ORE_DAWN_STONE = this.of("dawn_stone")
    val ORE_DUSK_STONE = this.of("dusk_stone")
    val ORE_FIRE_STONE = this.of("fire_stone")
    val ORE_ICE_STONE = this.of("ice_stone")
    val ORE_LEAF_STONE = this.of("leaf_stone")
    val ORE_MOON_STONE = this.of("moon_stone")
    // We want this to be a separate configured feature to avoid generating too much moon stone
    val ORE_MOON_STONE_DRIPSTONE = this.of("dripstone_moon_stone")
    val ORE_SHINY_STONE = this.of("shiny_stone")
    val ORE_SUN_STONE = this.of("sun_stone")
    val ORE_THUNDER_STONE = this.of("thunder_stone")
    val ORE_WATER_STONE = this.of("water_stone")

    private fun of(id: String): RegistryKey<ConfiguredFeature<*, *>> = ConfiguredFeatures.of("${Cobblemon.MODID}:ore/$id")

}