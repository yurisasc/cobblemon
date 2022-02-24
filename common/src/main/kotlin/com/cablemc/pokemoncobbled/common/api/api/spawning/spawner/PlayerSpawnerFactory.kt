package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemoncobbled.common.api.spawning.influence.PlayerLevelRangeInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import com.cablemc.pokemoncobbled.common.spawning.CobbledSpawnPools
import net.minecraft.server.level.ServerPlayer

/**
 * Responsible for creating [PlayerSpawner]s with whatever appropriate settings. You can
 * swap over the spawn pool and the influences here and it will apply to all newly-generated
 * [PlayerSpawner]s.
 *
 * @author Hiroku
 * @since February 14th, 2022
 */
object PlayerSpawnerFactory {
    var spawns: SpawnPool = CobbledSpawnPools.WORLD_SPAWN_POOL
    var influenceBuilders = mutableListOf<(player: ServerPlayer) -> SpawningInfluence?>({ PlayerLevelRangeInfluence(it, variation = 5) })

    fun create(spawnerManager: SpawnerManager, player: ServerPlayer): PlayerSpawner {
        val influences = influenceBuilders.mapNotNull { it(player) }
        return PlayerSpawner(player, spawns, spawnerManager).also {
            it.influences.addAll(influences)
        }
    }
}