package com.cablemc.pokemoncobbled.common.spawning

import com.cablemc.pokemoncobbled.common.spawning.utils.BiomeHelper
import com.cablemc.pokemoncobbled.common.spawning.utils.LocationHelper
import com.cablemc.pokemoncobbled.common.util.blockPos
import net.minecraft.world.entity.player.Player

/**
 * Implementation of the ISpawner as variation
 * This variation spawns a Pokémon near a given Player and inside the parameters set in Settings
 */
class NearPlayerSpawner: ISpawner<Player> {

    override fun spawnNear(entity: Player) {
        val spawnInfo = SpawnInfo(entity, LocationHelper.getGroundPos(getNearVector(entity), entity))

        val possibleSpawns = BiomeHelper.possibleSpawns(entity.level.getBiome(spawnInfo.spawnPos.blockPos()))

        if(possibleSpawns.isEmpty())
            return

        spawn(possibleSpawns.random(), spawnInfo.spawnPos, entity.level)
    }

}