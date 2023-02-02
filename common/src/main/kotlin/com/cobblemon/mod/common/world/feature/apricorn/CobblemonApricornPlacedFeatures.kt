/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.world.feature.apricorn

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.util.cobblemonResource
import dev.architectury.registry.level.biome.BiomeModifications
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig
import net.minecraft.world.gen.feature.VegetationPlacedFeatures
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier

object CobblemonApricornPlacedFeatures {

    val BLACK_APRICORN_TREE_PLACED_FEATURE = this.of("black_apricorn_tree")
    val BLUE_APRICORN_TREE_PLACED_FEATURE = this.of("blue_apricorn_tree")
    val GREEN_APRICORN_TREE_PLACED_FEATURE = this.of("green_apricorn_tree")
    val PINK_APRICORN_TREE_PLACED_FEATURE = this.of("pink_apricorn_tree")
    val RED_APRICORN_TREE_PLACED_FEATURE = this.of("red_apricorn_tree")
    val WHITE_APRICORN_TREE_PLACED_FEATURE = this.of("white_apricorn_tree")
    val YELLOW_APRICORN_TREE_PLACED_FEATURE = this.of("yellow_apricorn_tree")
    val APRICORN_TREES = this.of("apricorn_trees")

    fun register() {
        BiomeModifications.addProperties{ _, properties ->
            properties.generationProperties.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, APRICORN_TREES)
        }
    }

    private fun of(id: String): RegistryKey<PlacedFeature> = PlacedFeatures.of("${Cobblemon.MODID}:$id")

}