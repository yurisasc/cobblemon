package com.cablemc.pokemoncobbled.common.worldgen

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.core.Registry
import net.minecraft.tags.TagKey
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement
import net.minecraft.world.level.levelgen.placement.InSquarePlacement
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest

object EvolutionOres {

    /**
     * Dawn Stone
     */

    val DAWN_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.DAWN_STONE_ORE.get().defaultBlockState(),
        name = "ore_dawn_stone_normal",
        veinSize = 3,
        amountPerChunk = 12,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_DAWN_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_DAWN_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_dawn_stone_normal",
        tagKey = DAWN_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Dusk Stone
     */

    val DUSK_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.DUSK_STONE_ORE.get().defaultBlockState(),
        name = "ore_dusk_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_DUSK_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_DUSK_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_dusk_stone_normal",
        tagKey = DUSK_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    val DUSK_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.DUSK_STONE_ORE.get().defaultBlockState(),
        name = "ore_dusk_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_DUSK_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_DUSK_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_dusk_stone_rare",
        tagKey = DUSK_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Fire Stone
     */

    val FIRE_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.FIRE_STONE_ORE.get().defaultBlockState(),
        name = "ore_fire_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_FIRE_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_FIRE_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_fire_stone_normal",
        tagKey = FIRE_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Ice Stone
     */

    val ICE_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.ICE_STONE_ORE.get().defaultBlockState(),
        name = "ore_ice_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_ICE_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_ICE_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_ice_stone_normal",
        tagKey = ICE_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    val ICE_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.ICE_STONE_ORE.get().defaultBlockState(),
        name = "ore_ice_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_ICE_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_ICE_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_ice_stone_rare",
        tagKey = ICE_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Leaf Stone
     */

    val LEAF_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.LEAF_STONE_ORE.get().defaultBlockState(),
        name = "ore_leaf_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_LEAF_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_LEAF_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_leaf_stone_normal",
        tagKey = LEAF_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
        )
    )

    val LEAF_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.LEAF_STONE_ORE.get().defaultBlockState(),
        name = "ore_leaf_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_LEAF_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_LEAF_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_leaf_stone_rare",
        tagKey = LEAF_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Moon Stone
     */

    val MOON_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.MOON_STONE_ORE.get().defaultBlockState(),
        name = "ore_moon_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val MOON_STONE_ORE_DRIPSTONE = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.DRIPSTONE_MOON_STONE_ORE.get().defaultBlockState(),
        name = "ore_moon_stone_dripstone",
        ruleTest = TagMatchTest(TagKey.create(Registry.BLOCK_REGISTRY, cobbledResource("drip_stone_replaceables"))),
        veinSize = 3,
        amountPerChunk = 150,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()),
            InSquarePlacement.spread()
        ),
        useBiomeTagFilter = false
    )

    val DEEPSLATE_MOON_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_MOON_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_moon_stone_normal",
        tagKey = MOON_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    val MOON_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.MOON_STONE_ORE.get().defaultBlockState(),
        name = "ore_moon_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_MOON_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_MOON_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_moon_stone_rare",
        tagKey = MOON_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Shiny Stone
     */

    val SHINY_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.SHINY_STONE_ORE.get().defaultBlockState(),
        name = "ore_shiny_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_SHINY_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_SHINY_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_shiny_stone_normal",
        tagKey = SHINY_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Sun Stone
     */

    val SUN_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.SUN_STONE_ORE.get().defaultBlockState(),
        name = "ore_sun_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_SUN_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_SUN_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_sun_stone_normal",
        tagKey = SUN_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    val SUN_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.SUN_STONE_ORE.get().defaultBlockState(),
        name = "ore_sun_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_SUN_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_SUN_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_sun_stone_rare",
        tagKey = SUN_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Thunder Stone
     */

    val THUNDER_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.THUNDER_STONE_ORE.get().defaultBlockState(),
        name = "ore_thunder_stone_normal",
        veinSize = 3,
        amountPerChunk = 12,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_THUNDER_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_THUNDER_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_thunder_stone_normal",
        tagKey = THUNDER_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    /**
     * Water Stone
     */

    val WATER_STONE_ORE_OCEAN = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.WATER_STONE_ORE.get().defaultBlockState(),
        name = "ore_water_stone_ocean",
        veinSize = 3,
        amountPerChunk = 12,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_WATER_STONE_ORE_OCEAN = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_WATER_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_water_stone_ocean",
        tagKey = WATER_STONE_ORE_OCEAN.tagKey,
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    val WATER_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = CobbledBlocks.WATER_STONE_ORE.get().defaultBlockState(),
        name = "ore_water_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(320)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_WATER_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_WATER_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_water_stone_normal",
        tagKey = WATER_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )

    val DEEPSLATE_WATER_STONE_ORE_DEEPOCEAN = DeepslateOreGeneration(
        blockState = CobbledBlocks.DEEPSLATE_WATER_STONE_ORE.get().defaultBlockState(),
        name = "ore_deepslate_water_stone_deepocean",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0)),
            InSquarePlacement.spread()
        )
    )
}