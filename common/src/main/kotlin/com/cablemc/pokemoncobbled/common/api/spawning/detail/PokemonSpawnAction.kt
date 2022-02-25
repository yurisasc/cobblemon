package com.cablemc.pokemoncobbled.common.api.spawning.detail

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity

/**
 * A [SpawnAction] that will spawn a single [PokemonEntity].
 *
 * @author Hiroku
 * @since February 13th, 2022
 */
class PokemonSpawnAction(
    spawner: Spawner,
    ctx: SpawningContext,
    detail: PokemonSpawnDetail,
    /** The [PokemonProperties] that are about to be used. */
    var props: PokemonProperties = detail.pokemon.copy()
) : SpawnAction<PokemonEntity>(spawner, ctx, detail) {
    override fun createEntity(): PokemonEntity {
        if (props.level == null && detail is PokemonSpawnDetail) {
            props.level = detail.getDerivedLevelRange().random()
        }
        return props.createEntity(ctx.level)
    }
}