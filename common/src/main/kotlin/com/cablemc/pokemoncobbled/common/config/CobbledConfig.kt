package com.cablemc.pokemoncobbled.common.config

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.status.Statuses
import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategoryAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.IntRangeAdapter
import com.google.gson.GsonBuilder
import net.minecraft.text.Text

class CobbledConfig {
    companion object {
        val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapter(IntRange::class.java, IntRangeAdapter)
            .registerTypeAdapter(StarterCategory::class.java, StarterCategoryAdapter)
            .create()
    }

    @NodeCategory(Category.Pokemon)
    @IntConstraint(min = 1, max = 1000)
    var maxPokemonLevel = 100

    // TODO new types of constraint

    @NodeCategory(Category.Spawning)
    @IntConstraint(min = 1, max = 1000)
    var minimumLevelRangeMax = 15

    @NodeCategory(Category.Spawning)
    var enableSpawning = true

    @NodeCategory(Category.Spawning)
    var minimumDistanceBetweenEntities = 6.0

    @NodeCategory(Category.Spawning)
    var maxNearbyBlocksRange = 8

    @NodeCategory(Category.Spawning)
    var maxHorizontalSpace = 6

    @NodeCategory(Category.Spawning)
    var maxVerticalSpace = 8

    @NodeCategory(Category.Spawning)
    var worldSliceDiameter = 8

    @NodeCategory(Category.Spawning)
    var worldSliceHeight = 8

    @NodeCategory(Category.Spawning)
    var minimumSliceDistanceFromPlayer = 16F

    @NodeCategory(Category.Spawning)
    var maximumSliceDistanceFromPlayer = 28F

    @NodeCategory(Category.Spawning)
    var exportSpawnsToConfig = false

    @NodeCategory(Category.Battles)
    var autoUpdateShowdown = true

    @NodeCategory(Category.PassiveStatus)
    var passiveStatuses = mutableMapOf(
        Statuses.POISON.configEntry()
    )

    @NodeCategory(Category.Healing)
    var infiniteHealerCharge = false

    @NodeCategory(Category.Healing)
    var maxHealerCharge = 6.0f

    @NodeCategory(Category.Healing)
    var chargeGainedPerTick = 0.00008333333f

    @NodeCategory(Category.Healing)
    var defaultFaintTimer = 300

    @NodeCategory(Category.Healing)
    var faintAwakenHealthPercent = 0.2f

    @NodeCategory(Category.Healing)
    var healPercent = 0.05

    @NodeCategory(Category.Healing)
    var healTimer = 60


    @NodeCategory(Category.Starter)
    var starters = mutableListOf(
            StarterCategory(
                name = "Kanto",
                displayName = Text.of("Kanto"),
                pokemon = mutableListOf(
                    PokemonProperties().also { it.level = 5 ; it.species = "Bulbasaur" },
                    PokemonProperties().also { it.level = 5 ; it.species = "Charmander" },
                    PokemonProperties().also { it.level = 5 ; it.species = "Squirtle" }
                )
            ),
            StarterCategory(
                name = "Johto",
                displayName = Text.of("Johto"),
                pokemon = mutableListOf(
                    PokemonProperties().also { it.level = 5 ; it.species = "Chikorita" },
                    PokemonProperties().also { it.level = 5 ; it.species = "Cyndaquil" },
                    PokemonProperties().also { it.level = 5 ; it.species = "Totodile" }
                )
            )
        )
}