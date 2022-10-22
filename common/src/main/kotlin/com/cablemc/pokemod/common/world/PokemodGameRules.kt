/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world

import net.minecraft.world.GameRules

object PokemodGameRules {
    lateinit var DO_POKEMON_SPAWNING: GameRules.Key<GameRules.BooleanRule>

    fun register() {
        DO_POKEMON_SPAWNING = GameRules.register("doPokemonSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true))
    }
}