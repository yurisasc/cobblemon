package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.PlayerSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.PlayerSpawnerFactory
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.util.UUID

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

    @SubscribeEvent
    fun on(event: PlayerEvent.PlayerLoggedInEvent) {
        if (event.player !is ServerPlayer) {
            return
        }
        val spawner = PlayerSpawnerFactory.create(this, event.player as ServerPlayer)
        spawnersForPlayers[event.player.uuid] = spawner
        registerSpawner(spawner)
    }

    @SubscribeEvent
    fun on(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (event.player !is ServerPlayer) {
            return
        }
        val spawner = spawnersForPlayers[event.entity.uuid]
        if (spawner != null) {
            spawnersForPlayers.remove(event.entity.uuid)
            unregisterSpawner(spawner)
        }
    }
}