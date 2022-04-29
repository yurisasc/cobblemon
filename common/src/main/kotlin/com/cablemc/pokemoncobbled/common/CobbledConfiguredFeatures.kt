package com.cablemc.pokemoncobbled.common

import net.minecraft.util.registry.RegistryEntry
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.SingleStateFeatureConfig

object CobbledConfiguredFeatures {
    lateinit var BLACK_APRICORN_TREE: RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, *>>
    lateinit var BLUE_APRICORN_TREE: RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, *>>
    lateinit var GREEN_APRICORN_TREE: RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, *>>
    lateinit var PINK_APRICORN_TREE: RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, *>>
    lateinit var RED_APRICORN_TREE: RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, *>>
    lateinit var WHITE_APRICORN_TREE: RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, *>>
    lateinit var YELLOW_APRICORN_TREE: RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, *>>

    fun register() {
        BLACK_APRICORN_TREE = ConfiguredFeatures.register("black_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(CobbledBlocks.BLACK_APRICORN.get().defaultState))
        BLUE_APRICORN_TREE = ConfiguredFeatures.register("blue_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(CobbledBlocks.BLUE_APRICORN.get().defaultState))
        GREEN_APRICORN_TREE = ConfiguredFeatures.register("green_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(CobbledBlocks.GREEN_APRICORN.get().defaultState))
        PINK_APRICORN_TREE = ConfiguredFeatures.register("pink_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(CobbledBlocks.PINK_APRICORN.get().defaultState))
        RED_APRICORN_TREE = ConfiguredFeatures.register("red_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(CobbledBlocks.RED_APRICORN.get().defaultState))
        WHITE_APRICORN_TREE = ConfiguredFeatures.register("white_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(CobbledBlocks.WHITE_APRICORN.get().defaultState))
        YELLOW_APRICORN_TREE = ConfiguredFeatures.register("yellow_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(CobbledBlocks.YELLOW_APRICORN.get().defaultState))
    }
}