/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import dev.architectury.registry.level.biome.BiomeModifications
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.feature.RandomFeatureConfig
import net.minecraft.world.gen.feature.RandomFeatureEntry
import net.minecraft.world.gen.feature.VegetationPlacedFeatures
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier

object PokemodPlacements {

    lateinit var BLACK_APRICORN_TREE: RegistryEntry<PlacedFeature>
    lateinit var BLUE_APRICORN_TREE: RegistryEntry<PlacedFeature>
    lateinit var GREEN_APRICORN_TREE: RegistryEntry<PlacedFeature>
    lateinit var PINK_APRICORN_TREE: RegistryEntry<PlacedFeature>
    lateinit var RED_APRICORN_TREE: RegistryEntry<PlacedFeature>
    lateinit var WHITE_APRICORN_TREE: RegistryEntry<PlacedFeature>
    lateinit var YELLOW_APRICORN_TREE: RegistryEntry<PlacedFeature>

    lateinit var APRICORN_TREES: RegistryEntry<PlacedFeature>

    fun register() {
        BLACK_APRICORN_TREE = PlacedFeatures.register("black_apricorn_tree", com.cablemc.pokemod.common.PokemodConfiguredFeatures.BLACK_APRICORN_TREE, PlacedFeatures.wouldSurvive(
            PokemodBlocks.BLACK_APRICORN_SAPLING.get()))
        BLUE_APRICORN_TREE = PlacedFeatures.register("blue_apricorn_tree", com.cablemc.pokemod.common.PokemodConfiguredFeatures.BLUE_APRICORN_TREE, PlacedFeatures.wouldSurvive(
            PokemodBlocks.BLUE_APRICORN_SAPLING.get()))
        GREEN_APRICORN_TREE = PlacedFeatures.register("green_apricorn_tree", com.cablemc.pokemod.common.PokemodConfiguredFeatures.GREEN_APRICORN_TREE, PlacedFeatures.wouldSurvive(
            PokemodBlocks.GREEN_APRICORN_SAPLING.get()))
        PINK_APRICORN_TREE = PlacedFeatures.register("pink_apricorn_tree", com.cablemc.pokemod.common.PokemodConfiguredFeatures.PINK_APRICORN_TREE, PlacedFeatures.wouldSurvive(
            PokemodBlocks.PINK_APRICORN_SAPLING.get()))
        RED_APRICORN_TREE = PlacedFeatures.register("red_apricorn_tree", com.cablemc.pokemod.common.PokemodConfiguredFeatures.RED_APRICORN_TREE, PlacedFeatures.wouldSurvive(
            PokemodBlocks.RED_APRICORN_SAPLING.get()))
        WHITE_APRICORN_TREE = PlacedFeatures.register("white_apricorn_tree", com.cablemc.pokemod.common.PokemodConfiguredFeatures.WHITE_APRICORN_TREE, PlacedFeatures.wouldSurvive(
            PokemodBlocks.WHITE_APRICORN_SAPLING.get()))
        YELLOW_APRICORN_TREE = PlacedFeatures.register("yellow_apricorn_tree", com.cablemc.pokemod.common.PokemodConfiguredFeatures.YELLOW_APRICORN_TREE, PlacedFeatures.wouldSurvive(
            PokemodBlocks.YELLOW_APRICORN_SAPLING.get()))

        val apricornTreeVariety = ConfiguredFeatures.register(
            "apricorn_trees", Feature.RANDOM_SELECTOR, RandomFeatureConfig(
                listOf(
                    RandomFeatureEntry(BLACK_APRICORN_TREE, 0.04f),
                    RandomFeatureEntry(BLUE_APRICORN_TREE, 0.10f),
                    RandomFeatureEntry(GREEN_APRICORN_TREE, 0.15f),
                    RandomFeatureEntry(PINK_APRICORN_TREE, 0.12f),
                    RandomFeatureEntry(RED_APRICORN_TREE, 0.33f),
                    RandomFeatureEntry(WHITE_APRICORN_TREE, 0.20f),
                    RandomFeatureEntry(YELLOW_APRICORN_TREE, 0.06f)
                ), RED_APRICORN_TREE
            )
        )

        APRICORN_TREES = PlacedFeatures.register("apricorn_trees", apricornTreeVariety, VegetationPlacedFeatures.modifiers(RarityFilterPlacementModifier.of(8)))

        BiomeModifications.addProperties({ context -> true}, { context, properties ->
            properties.generationProperties.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, APRICORN_TREES)
        })
    }

}