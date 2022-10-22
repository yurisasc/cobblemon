/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.spawning.influence

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.Pokemod.config
import com.cablemc.pokemod.common.api.spawning.context.SpawningContext
import com.cablemc.pokemod.common.api.spawning.detail.PokemonSpawnAction
import com.cablemc.pokemod.common.api.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemod.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemod.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemod.common.util.math.intersection
import com.cablemc.pokemod.common.util.math.intersects
import kotlin.math.max
import kotlin.math.min
import net.minecraft.server.network.ServerPlayerEntity

/**
 * A [SpawningInfluence] that restricts spawns around a player to be within their level range.
 * This will flat out prevent Pokémon spawns that can't be within the level range, and then for
 * those that can be, it will also adjust the possible level range to be the intersection of the
 * acceptable level range and the player's level range.
 *
 * @author Hiroku
 * @since February 14th, 2022
 */
open class PlayerLevelRangeInfluence(
    player: ServerPlayerEntity,
    val variation: Int,
    val noPokemonRange: IntRange = 1 .. config.minimumLevelRangeMax,
    val recalculationMillis: Long = 5000L
) : SpawningInfluence {
    val uuid = player.uuid
    var lastCalculatedTime: Long = 0
    var previousRange: IntRange = noPokemonRange

    fun getPlayerLevelRange(): IntRange {
        return if (System.currentTimeMillis() - lastCalculatedTime > recalculationMillis) {
            lastCalculatedTime = System.currentTimeMillis()

            val party = Pokemod.storage.getParty(uuid)
            previousRange = if (party.any()) {
                val minimumLevel = party.minOf { it.level }
                val maximumLevel = party.maxOf { it.level }
                IntRange(max(minimumLevel - variation, 1), min(config.maxPokemonLevel, max(maximumLevel + variation, config.minimumLevelRangeMax)))
            } else {
                noPokemonRange
            }
            previousRange
        } else {
            previousRange
        }
    }

    override fun affectSpawnable(detail: SpawnDetail, ctx: SpawningContext): Boolean {
        return if (detail !is PokemonSpawnDetail) {
            true
        } else {
            val playerRange = getPlayerLevelRange()
            val spawnRange = detail.getDerivedLevelRange()

            playerRange.intersects(spawnRange)
        }
    }

    override fun affectAction(action: SpawnAction<*>) {
        if (action is PokemonSpawnAction && action.detail is PokemonSpawnDetail && action.props.level == null) {
            action.props.level = getPlayerLevelRange().intersection(action.detail.getDerivedLevelRange()).random()
        }
    }
}