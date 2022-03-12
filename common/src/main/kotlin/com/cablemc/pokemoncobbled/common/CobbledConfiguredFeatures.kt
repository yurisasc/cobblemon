package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.feature.ApricornTreeFeature
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

interface CobbledConfiguredFeatures {
    fun register();

    fun apricornTree() : ConfiguredFeature<NoneFeatureConfiguration, ApricornTreeFeature>
}