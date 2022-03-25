package com.cablemc.pokemoncobbled.common

import com.google.common.collect.ImmutableList
import dev.architectury.registry.level.biome.BiomeModifications
import net.minecraft.core.Holder
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.data.worldgen.placement.TreePlacements
import net.minecraft.data.worldgen.placement.VegetationPlacements
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
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

    private val APRICORN_TREES: Holder<ConfiguredFeature<RandomFeatureConfiguration, *>>  = FeatureUtils.register(
        "apricorn_trees", Feature.RANDOM_SELECTOR, RandomFeatureConfiguration(
            listOf(
                WeightedPlacedFeature(BLACK_APRICORN_TREE, 0.25f),
                WeightedPlacedFeature(BLUE_APRICORN_TREE, 0.25f),
                WeightedPlacedFeature(GREEN_APRICORN_TREE, 0.25f),
                WeightedPlacedFeature(PINK_APRICORN_TREE, 0.25f),
                WeightedPlacedFeature(RED_APRICORN_TREE, 0.25f),
                WeightedPlacedFeature(WHITE_APRICORN_TREE, 0.25f),
                WeightedPlacedFeature(YELLOW_APRICORN_TREE, 0.25f)
            ), YELLOW_APRICORN_TREE
        )
    )



    fun register() {
        //BLACK_APRICORN_TREE = PlacementUtils.register("black_apricorn_tree", PokemonCobbled.cobbledConfiguredFeatures.blackApricornTree(), PlacementUtils.filteredByBlockSurvival(CobbledBlocks.BLACK_APRICORN_SAPLING.get()))


//        BLACK_APRICORN_TREE = PlacementUtils.register("black_apricorn_tree", Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.blackApricornTree()),
//            VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1)))



        /*BLACK_APRICORN_TREE = PlacementUtils.register("black_apricorn_tree", Holder.direct(PokemonCobbled.cobbledConfiguredFeatures.blackApricornTree()),
            VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(8)))*/


        BiomeModifications.addProperties({ context -> true}, { context, properties ->
            properties.generationProperties.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, APRICORN_TREES)
            /*properties.generationProperties.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, BLUE_APRICORN_TREE)
            properties.generationProperties.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, GREEN_APRICORN_TREE)
            properties.generationProperties.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PINK_APRICORN_TREE)
            properties.generationProperties.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, RED_APRICORN_TREE)
            properties.generationProperties.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WHITE_APRICORN_TREE)
            properties.generationProperties.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, YELLOW_APRICORN_TREE)*/
        })
    }

}