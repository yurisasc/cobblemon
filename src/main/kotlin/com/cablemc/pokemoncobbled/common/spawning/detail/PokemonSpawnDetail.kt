package com.cablemc.pokemoncobbled.common.spawning.detail

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner

class PokemonSpawnDetail(override val type: String = TYPE) : SpawnDetail() {
    companion object {
        val TYPE = "pokemon"
    }

    override fun doSpawn(spawner: Spawner, ctx: SpawningContext): SpawnAction<*> {
        TODO("Not yet implemented")
    }
}