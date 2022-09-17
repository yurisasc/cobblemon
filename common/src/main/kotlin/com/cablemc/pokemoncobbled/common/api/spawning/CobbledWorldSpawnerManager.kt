/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.PlayerSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.PlayerSpawnerFactory
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.PlayerEvent.PLAYER_JOIN
import dev.architectury.event.events.common.PlayerEvent.PLAYER_QUIT
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

/**
 * The [SpawnerManager] that handles Cobbled's world spawner, which is made up
 * of [PlayerSpawner] instances. This manager listens for login and logout and
 * cleans up where relevant.
 *
 * @author Hiroku
 * @since February 14th, 2022
 */
object CobbledWorldSpawnerManager : SpawnerManager() {
    var spawnersForPlayers = mutableMapOf<UUID, PlayerSpawner>()

    val playerJoinListener = PlayerEvent.PlayerJoin(this::onPlayerLogin)
    val playerLeaveListener = PlayerEvent.PlayerQuit(this::onPlayerLogout)

    override fun onServerStarted() {
        super.onServerStarted()
        if (!PLAYER_JOIN.isRegistered(playerJoinListener)) {
            PLAYER_JOIN.register(playerJoinListener)
        }
        if (!PLAYER_QUIT.isRegistered(playerLeaveListener)) {
            PLAYER_QUIT.register(playerLeaveListener)
        }
    }

    fun onPlayerLogin(player: ServerPlayerEntity) {
        // Disables spawning
        if (!PokemonCobbled.config.enableSpawning) {
            return;
        }

        val spawner = PlayerSpawnerFactory.create(this, player)
        spawnersForPlayers[player.uuid] = spawner
        registerSpawner(spawner)
    }

    fun onPlayerLogout(player: ServerPlayerEntity) {
        val spawner = spawnersForPlayers[player.uuid]
        if (spawner != null) {
            spawnersForPlayers.remove(player.uuid)
            unregisterSpawner(spawner)
        }
    }
}