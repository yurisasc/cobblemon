package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.CobbledConfiguredFeatures
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.world.feature.ApricornTreeFeature
import net.minecraft.core.Registry
import net.minecraft.data.BuiltinRegistries.CONFIGURED_FEATURE
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

object FabricConfiguredFeatures : CobbledConfiguredFeatures {

    private fun <C, F, T : ConfiguredFeature<C, F>> register(name: String, feature: T) : T {
        return Registry.register(CONFIGURED_FEATURE, "${PokemonCobbled.MODID}:$name", feature)
    }

    private fun <F : Feature<NoneFeatureConfiguration>> register(name: String, feature: F): ConfiguredFeature<NoneFeatureConfiguration, F> {
        return ConfiguredFeature(feature, NoneFeatureConfiguration()).also { register(name, it) }
    }

    lateinit var APRICORN_TREE: ConfiguredFeature<NoneFeatureConfiguration, ApricornTreeFeature>

    override fun register() {
        APRICORN_TREE = register("", PokemonCobbled.cobbledFeatures.apricornTreeFeature())
    }

    override fun apricornTree(): ConfiguredFeature<NoneFeatureConfiguration, ApricornTreeFeature> {
        return APRICORN_TREE
    }
}