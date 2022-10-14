/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.generation

import com.cablemc.pokemod.common.PokemodBlocks
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.structure.rule.TagMatchRuleTest
import net.minecraft.tag.TagKey
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier

object EvolutionOres {

    /**
     * Dawn Stone
     */

    val DAWN_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.DAWN_STONE_ORE.get().defaultState,
        name = "ore_dawn_stone_normal",
        veinSize = 3,
        amountPerChunk = 12,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_DAWN_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_DAWN_STONE_ORE.get().defaultState,
        name = "ore_deepslate_dawn_stone_normal",
        tagKey = DAWN_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Dusk Stone
     */

    val DUSK_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.DUSK_STONE_ORE.get().defaultState,
        name = "ore_dusk_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_DUSK_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_DUSK_STONE_ORE.get().defaultState,
        name = "ore_deepslate_dusk_stone_normal",
        tagKey = DUSK_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    val DUSK_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.DUSK_STONE_ORE.get().defaultState,
        name = "ore_dusk_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_DUSK_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_DUSK_STONE_ORE.get().defaultState,
        name = "ore_deepslate_dusk_stone_rare",
        tagKey = DUSK_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Fire Stone
     */

    val FIRE_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.FIRE_STONE_ORE.get().defaultState,
        name = "ore_fire_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_FIRE_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_FIRE_STONE_ORE.get().defaultState,
        name = "ore_deepslate_fire_stone_normal",
        tagKey = FIRE_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Ice Stone
     */

    val ICE_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.ICE_STONE_ORE.get().defaultState,
        name = "ore_ice_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_ICE_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_ICE_STONE_ORE.get().defaultState,
        name = "ore_deepslate_ice_stone_normal",
        tagKey = ICE_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    val ICE_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.ICE_STONE_ORE.get().defaultState,
        name = "ore_ice_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_ICE_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_ICE_STONE_ORE.get().defaultState,
        name = "ore_deepslate_ice_stone_rare",
        tagKey = ICE_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Leaf Stone
     */

    val LEAF_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.LEAF_STONE_ORE.get().defaultState,
        name = "ore_leaf_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_LEAF_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_LEAF_STONE_ORE.get().defaultState,
        name = "ore_deepslate_leaf_stone_normal",
        tagKey = LEAF_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
        )
    )

    val LEAF_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.LEAF_STONE_ORE.get().defaultState,
        name = "ore_leaf_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_LEAF_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_LEAF_STONE_ORE.get().defaultState,
        name = "ore_deepslate_leaf_stone_rare",
        tagKey = LEAF_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Moon Stone
     */

    val MOON_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.MOON_STONE_ORE.get().defaultState,
        name = "ore_moon_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val MOON_STONE_ORE_DRIPSTONE = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.DRIPSTONE_MOON_STONE_ORE.get().defaultState,
        name = "ore_moon_stone_dripstone",
        ruleTest = TagMatchRuleTest(TagKey.of(Registry.BLOCK_KEY, pokemodResource("drip_stone_replaceables"))),
        veinSize = 3,
        amountPerChunk = 150,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop()),
            SquarePlacementModifier.of()
        ),
        useBiomeTagFilter = false
    )

    val DEEPSLATE_MOON_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_MOON_STONE_ORE.get().defaultState,
        name = "ore_deepslate_moon_stone_normal",
        tagKey = MOON_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    val MOON_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.MOON_STONE_ORE.get().defaultState,
        name = "ore_moon_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_MOON_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_MOON_STONE_ORE.get().defaultState,
        name = "ore_deepslate_moon_stone_rare",
        tagKey = MOON_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Shiny Stone
     */

    val SHINY_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.SHINY_STONE_ORE.get().defaultState,
        name = "ore_shiny_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_SHINY_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_SHINY_STONE_ORE.get().defaultState,
        name = "ore_deepslate_shiny_stone_normal",
        tagKey = SHINY_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Sun Stone
     */

    val SUN_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.SUN_STONE_ORE.get().defaultState,
        name = "ore_sun_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_SUN_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_SUN_STONE_ORE.get().defaultState,
        name = "ore_deepslate_sun_stone_normal",
        tagKey = SUN_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    val SUN_STONE_ORE_RARE = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.SUN_STONE_ORE.get().defaultState,
        name = "ore_sun_stone_rare",
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_SUN_STONE_ORE_RARE = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_SUN_STONE_ORE.get().defaultState,
        name = "ore_deepslate_sun_stone_rare",
        tagKey = SUN_STONE_ORE_RARE.tagKey,
        veinSize = 3,
        amountPerChunk = 2,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Thunder Stone
     */

    val THUNDER_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.THUNDER_STONE_ORE.get().defaultState,
        name = "ore_thunder_stone_normal",
        veinSize = 3,
        amountPerChunk = 12,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_THUNDER_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_THUNDER_STONE_ORE.get().defaultState,
        name = "ore_deepslate_thunder_stone_normal",
        tagKey = THUNDER_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    /**
     * Water Stone
     */

    val WATER_STONE_ORE_OCEAN = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.WATER_STONE_ORE.get().defaultState,
        name = "ore_water_stone_ocean",
        veinSize = 3,
        amountPerChunk = 12,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_WATER_STONE_ORE_OCEAN = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_WATER_STONE_ORE.get().defaultState,
        name = "ore_deepslate_water_stone_ocean",
        tagKey = WATER_STONE_ORE_OCEAN.tagKey,
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    val WATER_STONE_ORE_NORMAL = EvolutionOreGenerationBase(
        blockState = PokemodBlocks.WATER_STONE_ORE.get().defaultState,
        name = "ore_water_stone_normal",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(320)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_WATER_STONE_ORE_NORMAL = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_WATER_STONE_ORE.get().defaultState,
        name = "ore_deepslate_water_stone_normal",
        tagKey = WATER_STONE_ORE_NORMAL.tagKey,
        veinSize = 3,
        amountPerChunk = 5,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )

    val DEEPSLATE_WATER_STONE_ORE_DEEPOCEAN = DeepslateOreGeneration(
        blockState = PokemodBlocks.DEEPSLATE_WATER_STONE_ORE.get().defaultState,
        name = "ore_deepslate_water_stone_deepocean",
        veinSize = 3,
        amountPerChunk = 10,
        additionalModifiers = arrayOf(
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)),
            SquarePlacementModifier.of()
        )
    )
}