/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.config

import com.cablemc.pokemod.common.api.drop.ItemDropMethod
import com.cablemc.pokemod.common.api.pokemon.status.Statuses
import com.cablemc.pokemod.common.config.constraint.IntConstraint
import com.cablemc.pokemod.common.util.adapters.IntRangeAdapter
import com.google.gson.GsonBuilder
class PokemodConfig {
    companion object {
        val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapter(IntRange::class.java, IntRangeAdapter)
            .registerTypeAdapter(ItemDropMethod::class.java, ItemDropMethod.adapter)
            .create()
    }

    @NodeCategory(Category.Pokemon)
    @IntConstraint(min = 1, max = 1000)
    var maxPokemonLevel = 100

    @NodeCategory(Category.Pokemon)
    @IntConstraint(min = 0, max = 1000)
    var maxPokemonFriendship = 255

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

    @NodeCategory(Category.Storage)
    var storageFormat = "nbt"

    @NodeCategory(Category.Storage)
    var preventCompletePartyDeposit = true

    // TODO new types of constraint

    @NodeCategory(Category.Spawning)
    @IntConstraint(min = 1, max = 1000)
    var minimumLevelRangeMax = 15

    @NodeCategory(Category.Spawning)
    var enableSpawning = true

    @NodeCategory(Category.Spawning)
    var minimumDistanceBetweenEntities = 8.0

    @NodeCategory(Category.Spawning)
    var maxNearbyBlocksHorizontalRange = 4

    @NodeCategory(Category.Spawning)
    var maxNearbyBlocksVerticalRange = 2

    @NodeCategory(Category.Spawning)
    var maxHorizontalSpace = 6

    @NodeCategory(Category.Spawning)
    var maxVerticalSpace = 8

    @NodeCategory(Category.Spawning)
    var worldSliceDiameter = 8

    @NodeCategory(Category.Spawning)
    var worldSliceHeight = 16

    @NodeCategory(Category.Spawning)
    var minimumSliceDistanceFromPlayer = 16 * 2F

    @NodeCategory(Category.Spawning)
    var maximumSliceDistanceFromPlayer = 16 * 6F

    @NodeCategory(Category.Spawning)
    var exportSpawnsToConfig = false

    @NodeCategory(Category.Spawning)
    var exportSpawnConfigToConfig = false

    @NodeCategory(Category.Battles)
    var autoUpdateShowdown = true

    @NodeCategory(Category.Battles)
    var defaultFleeDistance = 16F * 2

    @NodeCategory(category = Category.Battles)
    var allowExperienceFromPvP = true

    @NodeCategory(Category.PassiveStatus)
    var passiveStatuses = mutableMapOf(
        Statuses.POISON.configEntry(),
        Statuses.POISON_BADLY.configEntry(),
        Statuses.PARALYSIS.configEntry(),
        Statuses.FROZEN.configEntry(),
        Statuses.SLEEP.configEntry(),
        Statuses.BURN.configEntry()
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

    @NodeCategory(Category.Spawning)
    var baseApricornTreeGenerationChance = 0.1F

    @NodeCategory(Category.Pokemon)
    var globalFlagSpeciesFeatures = mutableListOf<String>()

    @NodeCategory(Category.Pokemon)
    var flagSpeciesFeatures = mutableListOf("sunglasses")

    @NodeCategory(Category.Pokemon)
    var ninjaskCreatesShedinja = true

    @NodeCategory(Category.Pokemon)
    var displayEntityLevelLabel = true

    @NodeCategory(Category.Spawning)
    var shinyRate = 8192F
}