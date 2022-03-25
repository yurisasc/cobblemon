package com.cablemc.pokemoncobbled.fabric

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.cablemc.pokemoncobbled.common.CobbledConfiguredFeatures
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.world.level.levelgen.feature.ApricornTreeFeature
import net.minecraft.core.Registry
import net.minecraft.data.BuiltinRegistries.CONFIGURED_FEATURE
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration

object FabricConfiguredFeatures : CobbledConfiguredFeatures {

    private fun <C, F, T : ConfiguredFeature<C, F>> register(name: String, feature: T): T {
        return Registry.register(CONFIGURED_FEATURE, "${PokemonCobbled.MODID}:$name", feature)
    }

    private fun <C : FeatureConfiguration, F : Feature<C>> register(name: String, feature: F, featureConfiguration: C): ConfiguredFeature<C, F> {
        return ConfiguredFeature(feature, featureConfiguration).also { register(name, it) }
    }

    lateinit var BLACK_APRICORN_TREE: ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    lateinit var BLUE_APRICORN_TREE: ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    lateinit var GREEN_APRICORN_TREE: ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    lateinit var PINK_APRICORN_TREE: ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    lateinit var RED_APRICORN_TREE: ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    lateinit var WHITE_APRICORN_TREE: ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>
    lateinit var YELLOW_APRICORN_TREE: ConfiguredFeature<BlockStateConfiguration, ApricornTreeFeature>

    override fun register() {
        BLACK_APRICORN_TREE = register("black_apricorn_tree", PokemonCobbled.cobbledFeatures.apricornTreeFeature(), BlockStateConfiguration(CobbledBlocks.BLACK_APRICORN.get().defaultBlockState()))
        BLUE_APRICORN_TREE = register("blue_apricorn_tree", PokemonCobbled.cobbledFeatures.apricornTreeFeature(), BlockStateConfiguration(CobbledBlocks.BLUE_APRICORN.get().defaultBlockState()))
        GREEN_APRICORN_TREE = register("green_apricorn_tree", PokemonCobbled.cobbledFeatures.apricornTreeFeature(), BlockStateConfiguration(CobbledBlocks.GREEN_APRICORN.get().defaultBlockState()))
        PINK_APRICORN_TREE = register("pink_apricorn_tree", PokemonCobbled.cobbledFeatures.apricornTreeFeature(), BlockStateConfiguration(CobbledBlocks.PINK_APRICORN.get().defaultBlockState()))
        RED_APRICORN_TREE = register("red_apricorn_tree", PokemonCobbled.cobbledFeatures.apricornTreeFeature(), BlockStateConfiguration(CobbledBlocks.RED_APRICORN.get().defaultBlockState()))
        WHITE_APRICORN_TREE = register("white_apricorn_tree", PokemonCobbled.cobbledFeatures.apricornTreeFeature(), BlockStateConfiguration(CobbledBlocks.WHITE_APRICORN.get().defaultBlockState()))
        YELLOW_APRICORN_TREE = register("yellow_apricorn_tree", PokemonCobbled.cobbledFeatures.apricornTreeFeature(), BlockStateConfiguration(CobbledBlocks.YELLOW_APRICORN.get().defaultBlockState()))
    }

    override fun blackApricornTree() = BLACK_APRICORN_TREE
    override fun blueApricornTree() = BLUE_APRICORN_TREE
    override fun greenApricornTree() = GREEN_APRICORN_TREE
    override fun pinkApricornTree() = PINK_APRICORN_TREE
    override fun redApricornTree() = RED_APRICORN_TREE
    override fun whiteApricornTree() = WHITE_APRICORN_TREE
    override fun yellowApricornTree() = YELLOW_APRICORN_TREE
}