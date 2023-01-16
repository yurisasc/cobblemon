/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.util.cobblemonResource
import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.world.gen.feature.BasaltPillarFeature
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.SingleStateFeatureConfig
import net.minecraft.world.gen.feature.TreeFeatureConfig

object CobblemonConfiguredFeatures {
    val BLACK_APRICORN_TREE_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, cobblemonResource("black_apricorn_tree"))
    val BLUE_APRICORN_TREE_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, cobblemonResource("blue_apricorn_tree"))
    val GREEN_APRICORN_TREE_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, cobblemonResource("green_apricorn_tree"))
    val PINK_APRICORN_TREE_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, cobblemonResource("pink_apricorn_tree"))
    val RED_APRICORN_TREE_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, cobblemonResource("red_apricorn_tree"))
    val WHITE_APRICORN_TREE_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, cobblemonResource("white_apricorn_tree"))
    val YELLOW_APRICORN_TREE_KEY = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, cobblemonResource("yellow_apricorn_tree"))
//
//    lateinit var BLACK_APRICORN_TREE: RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, *>>
//    lateinit var BLUE_APRICORN_TREE: RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, *>>
//    lateinit var GREEN_APRICORN_TREE: RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, *>>
//    lateinit var PINK_APRICORN_TREE: RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, *>>
//    lateinit var RED_APRICORN_TREE: RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, *>>
//    lateinit var WHITE_APRICORN_TREE: RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, *>>
//    lateinit var YELLOW_APRICORN_TREE: RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, *>>

    fun register() {
//        BLACK_APRICORN_TREE = ConfiguredFeatures.register(feature, BLACK_APRICORN_TREE_KEY, feature.feature, feature.config)
//        BLUE_APRICORN_TREE = ConfiguredFeatures.register("blue_apricorn_tree", CobblemonFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(
//            CobblemonBlocks.BLUE_APRICORN.get().defaultState))
//        GREEN_APRICORN_TREE = ConfiguredFeatures.register("green_apricorn_tree", CobblemonFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(
//            CobblemonBlocks.GREEN_APRICORN.get().defaultState))
//        PINK_APRICORN_TREE = ConfiguredFeatures.register("pink_apricorn_tree", CobblemonFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(
//            CobblemonBlocks.PINK_APRICORN.get().defaultState))
//        RED_APRICORN_TREE = ConfiguredFeatures.register("red_apricorn_tree", CobblemonFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(
//            CobblemonBlocks.RED_APRICORN.get().defaultState))
//        WHITE_APRICORN_TREE = ConfiguredFeatures.register("white_apricorn_tree", CobblemonFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(
//            CobblemonBlocks.WHITE_APRICORN.get().defaultState))
//        YELLOW_APRICORN_TREE = ConfiguredFeatures.register("yellow_apricorn_tree", CobblemonFeatures.APRICORN_TREE_FEATURE.get(), SingleStateFeatureConfig(
//            CobblemonBlocks.YELLOW_APRICORN.get().defaultState))
    }
}