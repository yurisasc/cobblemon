package com.cablemc.pokemoncobbled.common.worldgen

import com.cablemc.pokemoncobbled.common.worldgen.placement.CobbledPlacementTypes
import dev.architectury.registry.level.biome.BiomeModifications
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES
import net.minecraft.world.level.levelgen.placement.BiomeFilter
import net.minecraft.world.level.levelgen.placement.CountPlacement
import net.minecraft.world.level.levelgen.placement.InSquarePlacement
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.RarityFilter

object CobbledWorldgen {

    init {
        EvolutionOres
    }

    fun register() {
        CobbledPlacementTypes.register()
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
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.LEAF_STONE_ORE_LUSH.placedFeature)

            // Moon Stone
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.MOON_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_MOON_STONE_ORE_NORMAL.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.MOON_STONE_ORE_RARE.placedFeature)
            properties.generationProperties.addFeature(UNDERGROUND_ORES, EvolutionOres.DEEPSLATE_MOON_STONE_ORE_RARE.placedFeature)

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
        return listOf(placementModifier, InSquarePlacement.spread(), placementModifier2, BiomeFilter.biome())
    }

    fun commonOrePlacement(i: Int, placementModifier: PlacementModifier): List<PlacementModifier> {
        return orePlacement(CountPlacement.of(i), placementModifier)
    }

    fun rareOrePlacement(i: Int, placementModifier: PlacementModifier): List<PlacementModifier> {
        return orePlacement(RarityFilter.onAverageOnceEvery(i), placementModifier)
    }
}