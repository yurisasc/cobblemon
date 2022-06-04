package com.cablemc.pokemoncobbled.common.api.spawning.influence

import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.world.CobbledGameRules.DO_POKEMON_SPAWNING

open class GameRuleInfluence : SpawningInfluence {
    override fun affectSpawnable(detail: SpawnDetail, ctx: SpawningContext): Boolean {
        return detail !is PokemonSpawnDetail || ctx.world.gameRules.getBoolean(DO_POKEMON_SPAWNING)
    }
}