/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.spawning.detail

import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.api.spawning.context.SpawningContext
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity

/**
 * A [SpawnAction] that will spawn a single [PokemonEntity].
 *
 * @author Hiroku
 * @since February 13th, 2022
 */
class PokemonSpawnAction(
    ctx: SpawningContext,
    override val detail: PokemonSpawnDetail,
    /** The [PokemonProperties] that are about to be used. */
    var props: PokemonProperties = detail.pokemon.copy()
) : SpawnAction<PokemonEntity>(ctx, detail) {
    override fun createEntity(): PokemonEntity {
        if (props.level == null) {
            props.level = detail.getDerivedLevelRange().random()
        }

        val entity = props.createEntity(ctx.world)
        entity.drops = detail.drops
        return entity
    }
}