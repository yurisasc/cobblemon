package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.TickingSpawner

/**
 * A manager of various spawners. This is a class in which you should register
 * ticking spawners so that they can be automatically ticked by the server.
 * This also supports registering [SpawningInfluence]s to apply to each spawner
 * that is registered.
 *
 * @author Hiroku
 * @since February 5th, 2022
 */
open class SpawnerManager {
    val spawners = mutableListOf<Spawner>()
    val influences = mutableListOf<SpawningInfluence>()

    inline fun <reified T : Spawner> getSpawnersOfType() = spawners.filterIsInstance<T>()
    open fun getSpawnerByName(name: String) = spawners.find { it.name == name }

    open fun registerSpawner(spawner: Spawner) {
        spawners.add(spawner)
        spawner.influences.addAll(influences)
    }

    open fun unregisterSpawner(spawner: Spawner) {
        spawners.remove(spawner)
        spawner.influences.removeAll(influences)
    }

    open fun onServerStarted() {
        spawners.clear()
    }

    open fun onServerTick() {
        // Disables spawning
        if(!PokemonCobbled.config.enableSpawning) {
            return;
        }
        getSpawnersOfType<TickingSpawner>().forEach { it.tick() }
    }
}