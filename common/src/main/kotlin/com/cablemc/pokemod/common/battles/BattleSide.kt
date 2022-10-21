/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.battles

import com.cablemc.pokemod.common.api.battles.model.PokemonBattle
import com.cablemc.pokemod.common.api.battles.model.actor.BattleActor

/**
 * Unlike the Showdown side.ts, this can represent multiple actors.
 *
 * @author Hiroku
 * @since March 9th, 2022
 */
class BattleSide(vararg val actors: BattleActor) {
    val activePokemon: List<ActiveBattlePokemon>
        get() = actors.flatMap { it.activePokemon }

    lateinit var battle: PokemonBattle
    fun getOppositeSide() = if (this == battle.side1) battle.side2 else battle.side1
}