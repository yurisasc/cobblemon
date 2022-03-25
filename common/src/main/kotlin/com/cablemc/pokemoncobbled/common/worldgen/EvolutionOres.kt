package com.cablemc.pokemoncobbled.common.worldgen

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.worldgen.placement.IsBiomeTagFilter
import net.minecraft.core.Registry
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.features.OreFeatures.STONE_ORE_REPLACEABLES
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.tags.TagKey
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.placement.CountPlacement
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement

object EvolutionOres {
    val FIRE_STONE_TAG = TagKey.create(Registry.BIOME_REGISTRY, cobbledResource("has_ore/fire_stone"))

    val FIRE_STONE_OVERWORLD = FeatureUtils.register(
        "ore_fire_stone",
        Feature.ORE,
        OreConfiguration(
            STONE_ORE_REPLACEABLES,
            CobbledBlocks.FIRE_STONE_ORE.get().defaultBlockState(),
            9
        )
    )

    val PLACED_FEATURE = PlacementUtils.register(
        "ore_fire_stone",
        FIRE_STONE_OVERWORLD,
        CountPlacement.of(20),
        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(160)),
        IsBiomeTagFilter(FIRE_STONE_TAG)
    )
}