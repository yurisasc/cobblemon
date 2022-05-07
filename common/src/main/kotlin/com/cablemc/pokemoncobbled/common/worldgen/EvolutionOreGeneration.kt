package com.cablemc.pokemoncobbled.common.worldgen

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.worldgen.placement.IsBiomeTagFilter
import net.minecraft.block.BlockState
import net.minecraft.structure.rule.RuleTest
import net.minecraft.tag.TagKey
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES
import net.minecraft.world.gen.feature.OreConfiguredFeatures.STONE_ORE_REPLACEABLES
import net.minecraft.world.gen.feature.OreFeatureConfig
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier
import net.minecraft.world.gen.placementmodifier.PlacementModifier

open class EvolutionOreGenerationBase(
    blockState: BlockState,
    name: String,
    val tagKey: TagKey<Biome> = TagKey.of(Registry.BIOME_KEY, cobbledResource("has_ore/$name")),
    veinSize: Int,
    discardChanceOnAirExposure: Float = 0.0F,
    amountPerChunk: Int,
    ruleTest: RuleTest = STONE_ORE_REPLACEABLES,
    vararg additionalModifiers: PlacementModifier,
    useBiomeTagFilter: Boolean = true
) {

    val configuredFeature = ConfiguredFeatures.register(
        name,
        Feature.ORE,
        OreFeatureConfig(
            ruleTest,
            blockState,
            veinSize,
            discardChanceOnAirExposure
        )
    )

    val placedFeature = if (useBiomeTagFilter)
        PlacedFeatures.register(
            name,
            configuredFeature,
            CountPlacementModifier.of(amountPerChunk),
            IsBiomeTagFilter(tagKey),
            *additionalModifiers
        )
    else
        PlacedFeatures.register(
            name,
            configuredFeature,
            CountPlacementModifier.of(amountPerChunk),
            *additionalModifiers
        )

}

class DeepslateOreGeneration(
    blockState: BlockState,
    name: String,
    tagKey: TagKey<Biome> = TagKey.of(Registry.BIOME_KEY, cobbledResource("has_ore/$name")),
    veinSize: Int,
    discardChanceOnAirExposure: Float = 0.0F,
    amountPerChunk: Int,
    vararg additionalModifiers: PlacementModifier
) : EvolutionOreGenerationBase(blockState, name, tagKey, veinSize, discardChanceOnAirExposure, amountPerChunk, DEEPSLATE_ORE_REPLACEABLES, *additionalModifiers)