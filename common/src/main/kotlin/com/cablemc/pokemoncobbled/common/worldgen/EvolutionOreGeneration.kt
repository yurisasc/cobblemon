package com.cablemc.pokemoncobbled.common.worldgen

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.worldgen.placement.IsBiomeTagFilter
import net.minecraft.core.Registry
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.features.OreFeatures.DEEPSLATE_ORE_REPLACEABLES
import net.minecraft.data.worldgen.features.OreFeatures.STONE_ORE_REPLACEABLES
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.placement.CountPlacement
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest

open class EvolutionOreGenerationBase(
    blockState: BlockState,
    name: String,
    val tagKey: TagKey<Biome> = TagKey.create(Registry.BIOME_REGISTRY, cobbledResource("has_ore/$name")),
    veinSize: Int,
    discardChanceOnAirExposure: Float = 0.0F,
    amountPerChunk: Int,
    ruleTest: RuleTest = STONE_ORE_REPLACEABLES,
    vararg additionalModifiers: PlacementModifier
) {

    val configuredFeature = FeatureUtils.register(
        name,
        Feature.ORE,
        OreConfiguration(
            ruleTest,
            blockState,
            veinSize,
            discardChanceOnAirExposure
        )
    )

    val placedFeature = PlacementUtils.register(
        name,
        configuredFeature,
        CountPlacement.of(amountPerChunk),
        IsBiomeTagFilter(tagKey),
        *additionalModifiers
    )
}

class DeepslateOreGeneration(
    blockState: BlockState,
    name: String,
    tagKey: TagKey<Biome> = TagKey.create(Registry.BIOME_REGISTRY, cobbledResource("has_ore/$name")),
    veinSize: Int,
    discardChanceOnAirExposure: Float = 0.0F,
    amountPerChunk: Int,
    vararg additionalModifiers: PlacementModifier
) : EvolutionOreGenerationBase(blockState, name, tagKey, veinSize, discardChanceOnAirExposure, amountPerChunk, DEEPSLATE_ORE_REPLACEABLES, *additionalModifiers)