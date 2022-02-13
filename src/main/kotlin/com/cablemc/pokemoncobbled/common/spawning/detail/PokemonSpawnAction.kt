package com.cablemc.pokemoncobbled.common.spawning.detail

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity

class PokemonSpawnAction(
    spawner: Spawner,
    ctx: SpawningContext,
    detail: PokemonSpawnDetail
) : SpawnAction<PokemonEntity>(spawner, ctx, detail) {


    override fun run() {
        TODO("Not yet implemented")
    }
}