package com.cablemc.pokemoncobbled.common.api.spawning.influence

import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.world.CobbledGameRules.DO_POKEMON_SPAWNING
import net.minecraft.server.network.ServerPlayerEntity

open class GameRuleInfluence(player: ServerPlayerEntity) : SpawningInfluence {
    private val canSpawn = player.world.gameRules.getBoolean(DO_POKEMON_SPAWNING)

    override fun affectSpawnable(detail: SpawnDetail): Boolean {
        return canSpawn
    }
}