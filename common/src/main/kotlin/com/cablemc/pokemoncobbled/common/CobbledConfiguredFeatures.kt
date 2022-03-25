package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.level.levelgen.feature.ApricornTreeFeature
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

interface CobbledConfiguredFeatures {
    fun register()

    fun blackApricornTree() : ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    fun blueApricornTree() : ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    fun greenApricornTree() : ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    fun pinkApricornTree() : ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    fun redApricornTree() : ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    fun whiteApricornTree() : ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    fun yellowApricornTree() : ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
}