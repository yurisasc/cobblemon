package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.CobbledConfiguredFeatures
import com.cablemc.pokemoncobbled.common.CobbledFeatures
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.world.feature.ApricornTreeFeature
import net.minecraft.core.Registry
import net.minecraft.data.BuiltinRegistries
import net.minecraft.data.BuiltinRegistries.CONFIGURED_FEATURE
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

object FabricConfiguredFeatures : CobbledConfiguredFeatures {

    private fun <T : ConfiguredFeature<*, *>> queue(name: String, feature: T) : T {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "${PokemonCobbled.MODID}:$name", feature)
    }

    val APRICORN_TREE = queue("", PokemonCobbled.cobbledFeatures.apricornTreeFeature().configured(NoneFeatureConfiguration()))

    override fun register() {
        // empty method to load class, maybe there's a better way? can't remember
    }

    override fun apricornTree(): ConfiguredFeature<NoneFeatureConfiguration, ApricornTreeFeature> {
        return APRICORN_TREE as ConfiguredFeature<NoneFeatureConfiguration, ApricornTreeFeature>
    }

}