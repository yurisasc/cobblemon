/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.CobbledSpawnPools
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemoncobbled.common.api.spawning.influence.GameRuleInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.influence.PlayerLevelRangeInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Responsible for creating [PlayerSpawner]s with whatever appropriate settings. You can
 * swap over the spawn pool and the influences here, and it will apply to all newly-generated
 * [PlayerSpawner]s.
 *
 * @author Hiroku
 * @since February 14th, 2022
 */
object PlayerSpawnerFactory {
    var spawns: SpawnPool = CobbledSpawnPools.WORLD_SPAWN_POOL
    var influenceBuilders = mutableListOf<(player: ServerPlayerEntity) -> SpawningInfluence?>({ PlayerLevelRangeInfluence(it, variation = 5) }, { GameRuleInfluence() })

    fun create(spawnerManager: SpawnerManager, player: ServerPlayerEntity): PlayerSpawner {
        val influences = influenceBuilders.mapNotNull { it(player) }
        return PlayerSpawner(player, spawns, spawnerManager).also {
            it.influences.addAll(influences)
        }
    }
}