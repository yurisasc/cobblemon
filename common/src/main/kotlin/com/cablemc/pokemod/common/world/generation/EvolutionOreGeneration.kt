/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.generation

import com.cablemc.pokemod.common.util.pokemodResource
import com.cablemc.pokemod.common.world.placement.IsBiomeTagFilter
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
    val tagKey: TagKey<Biome> = TagKey.of(Registry.BIOME_KEY, pokemodResource("has_ore/$name")),
    veinSize: Int,
    discardChanceOnAirExposure: Float = 0.0F,
    amountPerChunk: Int,
    ruleTest: RuleTest = STONE_ORE_REPLACEABLES,
    vararg additionalModifiers: PlacementModifier,
    useBiomeTagFilter: Boolean = true
) {

    val configuredFeature = ConfiguredFeatures.register(
        pokemodResource(name).toString(),
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
            pokemodResource(name).toString(),
            configuredFeature,
            CountPlacementModifier.of(amountPerChunk),
            IsBiomeTagFilter(tagKey),
            *additionalModifiers
        )
    else
        PlacedFeatures.register(
            pokemodResource(name).toString(),
            configuredFeature,
            CountPlacementModifier.of(amountPerChunk),
            *additionalModifiers
        )

}
class DeepslateOreGeneration(
    blockState: BlockState,
    name: String,
    tagKey: TagKey<Biome> = TagKey.of(Registry.BIOME_KEY, pokemodResource("has_ore/$name")),
    veinSize: Int,
    discardChanceOnAirExposure: Float = 0.0F,
    amountPerChunk: Int,
    vararg additionalModifiers: PlacementModifier
) : EvolutionOreGenerationBase(blockState, name, tagKey, veinSize, discardChanceOnAirExposure, amountPerChunk, DEEPSLATE_ORE_REPLACEABLES, *additionalModifiers)