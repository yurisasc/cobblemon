package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.level.levelgen.feature.ApricornTreeFeature
import net.minecraft.core.Registry
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration

object CobbledFeatures {
    private fun <T : Feature<*>> register(name: String, feature: T) : T {
        return Registry.register(Registry.FEATURE, "${PokemonCobbled.MODID}:$name", feature)
    }

    val APRICORN_TREE_FEATURE = register("apricorn_tree_feature", ApricornTreeFeature(BlockStateConfiguration.CODEC))

    fun register() {
        // empty method to load class, maybe there's a better way? can't remember
    }
}