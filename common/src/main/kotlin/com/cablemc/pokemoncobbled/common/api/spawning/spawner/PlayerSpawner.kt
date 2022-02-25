package com.cablemc.pokemoncobbled.common.api.spawning.spawner

import com.cablemc.pokemoncobbled.common.PokemonCobbled.config
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnPool
import com.cablemc.pokemoncobbled.common.util.getPlayer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth.ceil
import java.util.UUID

/**
 * A spawner that works around a single player. It will do basic tracking of a player's speed
 * and project that into the future and spawn in that direction.
 *
 * @author Hiroku
 * @since February 14th, 2022
 */
class PlayerSpawner(player: ServerPlayer, spawns: SpawnPool, manager: SpawnerManager) : AreaSpawner(player.name.string, spawns, manager) {
    val uuid: UUID = player.uuid

    override fun getArea(): SpawningArea? {
        val player = uuid.getPlayer() ?: return null
        val sliceDiameter = config.worldSliceDiameter
        val sliceHeight = config.worldSliceHeight

        // Probably means we need a large player motion factor
        val center = player.position().add(player.deltaMovement.scale(config.playerMotionFactor))

        return SpawningArea(
            cause = player,
            level = player.level,
            baseX = ceil(center.x - sliceDiameter / 2F),
            baseY = ceil(center.y - sliceHeight / 2F),
            baseZ = ceil(center.z - sliceDiameter / 2F),
            length = sliceDiameter,
            height = sliceHeight,
            width = sliceDiameter
        )
    }
}