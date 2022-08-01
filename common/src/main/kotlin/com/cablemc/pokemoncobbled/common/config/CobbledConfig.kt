package com.cablemc.pokemoncobbled.common.config

import com.cablemc.pokemoncobbled.common.api.drop.ItemDropMethod
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.status.Statuses
import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import com.cablemc.pokemoncobbled.common.util.adapters.IntRangeAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.pokemonPropertiesShortAdapter
import com.google.gson.GsonBuilder

class CobbledConfig {
    companion object {
        val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapter(IntRange::class.java, IntRangeAdapter)
            .registerTypeAdapter(ItemDropMethod::class.java, ItemDropMethod.adapter)
            .registerTypeAdapter(PokemonProperties::class.java, pokemonPropertiesShortAdapter)
            .create()
    }

    @NodeCategory(Category.Pokemon)
    @IntConstraint(min = 1, max = 1000)
    var maxPokemonLevel = 100

    @NodeCategory(Category.Pokemon)
    var announceDropItems = true
    @NodeCategory(Category.Pokemon)
    var defaultDropItemMethod = ItemDropMethod.ON_ENTITY

    @NodeCategory(Category.Storage)
    @IntConstraint(min = 1, max = 1000)
    var defaultBoxCount = 30
    @NodeCategory(Category.Storage)
    @IntConstraint(min = 1, max = 120)
    var pokemonSaveIntervalSeconds = 30

//    @NodeCategory(Category.Storage)
//    var storageFormat = "nbt"

    // TODO new types of constraint

    @NodeCategory(Category.Spawning)
    @IntConstraint(min = 1, max = 1000)
    var minimumLevelRangeMax = 15

    @NodeCategory(Category.Spawning)
    var enableSpawning = true

    @NodeCategory(Category.Spawning)
    var minimumDistanceBetweenEntities = 6.0

    @NodeCategory(Category.Spawning)
    var maxNearbyBlocksHorizontalRange = 4

    @NodeCategory(Category.Spawning)
    var maxNearbyBlocksVerticalRange = 2

    @NodeCategory(Category.Spawning)
    var maxHorizontalSpace = 6

    @NodeCategory(Category.Spawning)
    var maxVerticalSpace = 8

    @NodeCategory(Category.Spawning)
    var worldSliceDiameter = 12

    @NodeCategory(Category.Spawning)
    var worldSliceHeight = 8

    @NodeCategory(Category.Spawning)
    var minimumSliceDistanceFromPlayer = 16 * 1.5F

    @NodeCategory(Category.Spawning)
    var maximumSliceDistanceFromPlayer = 16 * 2.5F

    @NodeCategory(Category.Spawning)
    var exportSpawnsToConfig = false

    @NodeCategory(Category.Spawning)
    var exportSpawnConfigToConfig = false

    @NodeCategory(Category.Battles)
    var autoUpdateShowdown = true

    @NodeCategory(Category.PassiveStatus)
    var passiveStatuses = mutableMapOf(
        Statuses.POISON.configEntry(),
        Statuses.PARALYSIS.configEntry()
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
                displayName = "pokemoncobbled.starterselection.category.kanto",
                pokemon = mutableListOf(
                    PokemonProperties.parse("Bulbasaur level=5"),
                    PokemonProperties.parse("Charmander level=5"),
                    PokemonProperties.parse("Squirtle level=5"),
                )
            )//,
//            StarterCategory(
//                name = "Johto",
//                displayName = lang("starterselection.category.johto"),
//                pokemon = mutableListOf(
//                    PokemonProperties().also { it.level = 5 ; it.species = "Chikorita" },
//                    PokemonProperties().also { it.level = 5 ; it.species = "Cyndaquil" },
//                    PokemonProperties().also { it.level = 5 ; it.species = "Totodile" }
//                )
//            )
        )
}