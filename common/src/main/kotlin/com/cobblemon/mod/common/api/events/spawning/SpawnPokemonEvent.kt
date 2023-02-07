package com.cobblemon.mod.common.api.events.spawning

import com.cobblemon.mod.common.api.events.Cancelable
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction

/**
 * Event fired when a Pokémon is about to spawn. Canceling this event will prevent it from spawning.
 *
 * @author Hiroku
 * @since February 7th, 2023
 */
class SpawnPokemonEvent(
    val spawnAction: PokemonSpawnAction
) : Cancelable()