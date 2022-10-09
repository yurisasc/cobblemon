/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.generation

import com.cablemc.pokemod.common.world.placement.PokemodPlacementTypes
import dev.architectury.registry.level.biome.BiomeModifications
import net.minecraft.world.gen.GenerationStep.Feature.UNDERGROUND_ORES
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier
import net.minecraft.world.gen.placementmodifier.PlacementModifier
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier

object PokemodWorldGeneration {

    init {
        EvolutionOres
    }

    fun register() {
        PokemodPlacementTypes.register()
        BiomeModifications.addProperties { _, properties ->
            // Dawn Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DAWN_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_DAWN_STONE_ORE_NORMAL.placedFeature)

            // Dusk Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DUSK_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_DUSK_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DUSK_STONE_ORE_RARE.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_DUSK_STONE_ORE_RARE.placedFeature)

            // Fire Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.FIRE_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_FIRE_STONE_ORE_NORMAL.placedFeature)

            // Ice Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.ICE_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_ICE_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.ICE_STONE_ORE_RARE.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_ICE_STONE_ORE_RARE.placedFeature)

            // Leaf Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.LEAF_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_LEAF_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.LEAF_STONE_ORE_RARE.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_LEAF_STONE_ORE_RARE.placedFeature)

            // Moon Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.MOON_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_MOON_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.MOON_STONE_ORE_RARE.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_MOON_STONE_ORE_RARE.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.MOON_STONE_ORE_DRIPSTONE.placedFeature)

            // Shiny Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.SHINY_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_SHINY_STONE_ORE_NORMAL.placedFeature)

            // Sun Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.SUN_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_SUN_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.SUN_STONE_ORE_RARE.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_SUN_STONE_ORE_RARE.placedFeature)

            // Thunder Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.THUNDER_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_THUNDER_STONE_ORE_NORMAL.placedFeature)

            // Water Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.WATER_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_WATER_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.WATER_STONE_ORE_OCEAN.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_WATER_STONE_ORE_OCEAN.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_WATER_STONE_ORE_DEEPOCEAN.placedFeature)
        }
    }

    fun orePlacement(placementModifier: PlacementModifier, placementModifier2: PlacementModifier): List<PlacementModifier> {
        return listOf(placementModifier, SquarePlacementModifier.of(), placementModifier2, BiomePlacementModifier.of())
    }

    fun commonOrePlacement(i: Int, placementModifier: PlacementModifier): List<PlacementModifier> {
        return orePlacement(CountPlacementModifier.of(i), placementModifier)
    }

    fun rareOrePlacement(i: Int, placementModifier: PlacementModifier): List<PlacementModifier> {
        return orePlacement(RarityFilterPlacementModifier.of(i), placementModifier)
    }
}