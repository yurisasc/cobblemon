package com.cablemc.pokemoncobbled.common.spawning

import net.minecraft.world.entity.player.Player

/**
 * Just a testing object
 */
object SpawningTest {

    val nearPlayerSpawner = NearPlayerSpawner()

    fun trySpawn(player: Player) {
        nearPlayerSpawner.spawnNear(player)
    }

}