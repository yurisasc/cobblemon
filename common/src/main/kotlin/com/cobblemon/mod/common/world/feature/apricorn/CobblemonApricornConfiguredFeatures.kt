/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature.apricorn

import com.cobblemon.mod.common.Cobblemon
import net.minecraft.registry.RegistryKey
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.ConfiguredFeatures

object CobblemonApricornConfiguredFeatures {

    val BLACK_APRICORN_TREE_KEY = of("black_apricorn_tree")
    val BLUE_APRICORN_TREE_KEY = of("blue_apricorn_tree")
    val GREEN_APRICORN_TREE_KEY = of("green_apricorn_tree")
    val PINK_APRICORN_TREE_KEY = of("pink_apricorn_tree")
    val RED_APRICORN_TREE_KEY = of("red_apricorn_tree")
    val WHITE_APRICORN_TREE_KEY = of("white_apricorn_tree")
    val YELLOW_APRICORN_TREE_KEY = of("yellow_apricorn_tree")

    private fun of(id: String): RegistryKey<ConfiguredFeature<*, *>> = ConfiguredFeatures.of("${Cobblemon.MODID}:$id")

}