package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.CobbledFeatures
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.world.level.levelgen.feature.ApricornTreeFeature
import net.minecraft.core.Registry
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

object FabricFeatures : CobbledFeatures {

    private fun <T : Feature<*>> queue(name: String, feature: T) : T {
        return Registry.register(Registry.FEATURE, "${PokemonCobbled.MODID}:$name", feature)
    }

    val APRICORN_TREE_FEATURE = queue("apricorn_tree_feature", ApricornTreeFeature(BlockStateConfiguration.CODEC))

    override fun register() {
        // empty method to load class, maybe there's a better way? can't remember
    }

    override fun apricornTreeFeature(): ApricornTreeFeature {
        return APRICORN_TREE_FEATURE
    }


}