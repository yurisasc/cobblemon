package com.cablemc.pokemoncobbled.common

import net.minecraft.core.Holder
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration

object CobbledConfiguredFeatures {
    lateinit var BLACK_APRICORN_TREE: Holder<ConfiguredFeature<BlockStateConfiguration, *>>
    lateinit var BLUE_APRICORN_TREE: Holder<ConfiguredFeature<BlockStateConfiguration, *>>
    lateinit var GREEN_APRICORN_TREE: Holder<ConfiguredFeature<BlockStateConfiguration, *>>
    lateinit var PINK_APRICORN_TREE: Holder<ConfiguredFeature<BlockStateConfiguration, *>>
    lateinit var RED_APRICORN_TREE: Holder<ConfiguredFeature<BlockStateConfiguration, *>>
    lateinit var WHITE_APRICORN_TREE: Holder<ConfiguredFeature<BlockStateConfiguration, *>>
    lateinit var YELLOW_APRICORN_TREE: Holder<ConfiguredFeature<BlockStateConfiguration, *>>

    fun register() {
        BLACK_APRICORN_TREE = FeatureUtils.register("black_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), BlockStateConfiguration(CobbledBlocks.BLACK_APRICORN.get().defaultBlockState()))
        BLUE_APRICORN_TREE = FeatureUtils.register("blue_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), BlockStateConfiguration(CobbledBlocks.BLUE_APRICORN.get().defaultBlockState()))
        GREEN_APRICORN_TREE = FeatureUtils.register("green_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), BlockStateConfiguration(CobbledBlocks.GREEN_APRICORN.get().defaultBlockState()))
        PINK_APRICORN_TREE = FeatureUtils.register("pink_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), BlockStateConfiguration(CobbledBlocks.PINK_APRICORN.get().defaultBlockState()))
        RED_APRICORN_TREE = FeatureUtils.register("red_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), BlockStateConfiguration(CobbledBlocks.RED_APRICORN.get().defaultBlockState()))
        WHITE_APRICORN_TREE = FeatureUtils.register("white_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), BlockStateConfiguration(CobbledBlocks.WHITE_APRICORN.get().defaultBlockState()))
        YELLOW_APRICORN_TREE = FeatureUtils.register("yellow_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE.get(), BlockStateConfiguration(CobbledBlocks.YELLOW_APRICORN.get().defaultBlockState()))
    }
}