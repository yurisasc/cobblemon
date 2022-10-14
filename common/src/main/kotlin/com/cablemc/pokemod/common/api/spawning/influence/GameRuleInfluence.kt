/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.spawning.influence

import com.cablemc.pokemod.common.api.spawning.context.SpawningContext
import com.cablemc.pokemod.common.api.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemod.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemod.common.world.PokemodGameRules.DO_POKEMON_SPAWNING

open class GameRuleInfluence : SpawningInfluence {
    override fun affectSpawnable(detail: SpawnDetail, ctx: SpawningContext): Boolean {
        return detail !is PokemonSpawnDetail || ctx.world.gameRules.getBoolean(DO_POKEMON_SPAWNING)
    }
}