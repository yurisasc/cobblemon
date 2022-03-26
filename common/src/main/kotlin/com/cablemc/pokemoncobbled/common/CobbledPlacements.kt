package com.cablemc.pokemoncobbled.common

import dev.architectury.registry.level.biome.BiomeModifications
import net.minecraft.core.Holder
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.data.worldgen.placement.VegetationPlacements
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraft.world.level.levelgen.placement.RarityFilter

object CobbledPlacements {

    lateinit var BLACK_APRICORN_TREE: Holder<PlacedFeature>
    lateinit var BLUE_APRICORN_TREE: Holder<PlacedFeature>
    lateinit var GREEN_APRICORN_TREE: Holder<PlacedFeature>
    lateinit var PINK_APRICORN_TREE: Holder<PlacedFeature>
    lateinit var RED_APRICORN_TREE: Holder<PlacedFeature>
    lateinit var WHITE_APRICORN_TREE: Holder<PlacedFeature>
    lateinit var YELLOW_APRICORN_TREE: Holder<PlacedFeature>

    lateinit var APRICORN_TREES: Holder<PlacedFeature>

    fun register() {
        BLACK_APRICORN_TREE = PlacementUtils.register("black_apricorn_tree", CobbledConfiguredFeatures.BLACK_APRICORN_TREE, PlacementUtils.filteredByBlockSurvival(CobbledBlocks.BLACK_APRICORN_SAPLING))
        BLUE_APRICORN_TREE = PlacementUtils.register("blue_apricorn_tree", CobbledConfiguredFeatures.BLUE_APRICORN_TREE, PlacementUtils.filteredByBlockSurvival(CobbledBlocks.BLUE_APRICORN_SAPLING))
        GREEN_APRICORN_TREE = PlacementUtils.register("green_apricorn_tree", CobbledConfiguredFeatures.GREEN_APRICORN_TREE, PlacementUtils.filteredByBlockSurvival(CobbledBlocks.GREEN_APRICORN_SAPLING))
        PINK_APRICORN_TREE = PlacementUtils.register("pink_apricorn_tree", CobbledConfiguredFeatures.PINK_APRICORN_TREE, PlacementUtils.filteredByBlockSurvival(CobbledBlocks.PINK_APRICORN_SAPLING))
        RED_APRICORN_TREE = PlacementUtils.register("red_apricorn_tree", CobbledConfiguredFeatures.RED_APRICORN_TREE, PlacementUtils.filteredByBlockSurvival(CobbledBlocks.RED_APRICORN_SAPLING))
        WHITE_APRICORN_TREE = PlacementUtils.register("white_apricorn_tree", CobbledConfiguredFeatures.WHITE_APRICORN_TREE, PlacementUtils.filteredByBlockSurvival(CobbledBlocks.WHITE_APRICORN_SAPLING))
        YELLOW_APRICORN_TREE = PlacementUtils.register("yellow_apricorn_tree", CobbledConfiguredFeatures.YELLOW_APRICORN_TREE, PlacementUtils.filteredByBlockSurvival(CobbledBlocks.YELLOW_APRICORN_SAPLING))

        val apricornTreeVariety = FeatureUtils.register(
            "apricorn_trees", Feature.RANDOM_SELECTOR, RandomFeatureConfiguration(
                listOf(
                    WeightedPlacedFeature(BLACK_APRICORN_TREE, 0.04f),
                    WeightedPlacedFeature(BLUE_APRICORN_TREE, 0.10f),
                    WeightedPlacedFeature(GREEN_APRICORN_TREE, 0.15f),
                    WeightedPlacedFeature(PINK_APRICORN_TREE, 0.12f),
                    WeightedPlacedFeature(RED_APRICORN_TREE, 0.33f),
                    WeightedPlacedFeature(WHITE_APRICORN_TREE, 0.20f),
                    WeightedPlacedFeature(YELLOW_APRICORN_TREE, 0.06f)
                ), RED_APRICORN_TREE
            )
        )

        APRICORN_TREES = PlacementUtils.register("apricorn_trees", apricornTreeVariety, VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(8)))

        BiomeModifications.addProperties({ context -> true}, { context, properties ->
            properties.generationProperties.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, APRICORN_TREES)
        })
    }

}