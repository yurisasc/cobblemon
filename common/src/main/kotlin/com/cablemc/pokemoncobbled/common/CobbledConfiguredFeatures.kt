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
        BLACK_APRICORN_TREE = FeatureUtils.register("black_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE, BlockStateConfiguration(CobbledBlocks.BLACK_APRICORN.defaultBlockState()))
        BLUE_APRICORN_TREE = FeatureUtils.register("blue_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE, BlockStateConfiguration(CobbledBlocks.BLUE_APRICORN.defaultBlockState()))
        GREEN_APRICORN_TREE = FeatureUtils.register("green_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE, BlockStateConfiguration(CobbledBlocks.GREEN_APRICORN.defaultBlockState()))
        PINK_APRICORN_TREE = FeatureUtils.register("pink_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE, BlockStateConfiguration(CobbledBlocks.PINK_APRICORN.defaultBlockState()))
        RED_APRICORN_TREE = FeatureUtils.register("red_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE, BlockStateConfiguration(CobbledBlocks.RED_APRICORN.defaultBlockState()))
        WHITE_APRICORN_TREE = FeatureUtils.register("white_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE, BlockStateConfiguration(CobbledBlocks.WHITE_APRICORN.defaultBlockState()))
        YELLOW_APRICORN_TREE = FeatureUtils.register("yellow_apricorn_tree", CobbledFeatures.APRICORN_TREE_FEATURE, BlockStateConfiguration(CobbledBlocks.YELLOW_APRICORN.defaultBlockState()))
    }
}