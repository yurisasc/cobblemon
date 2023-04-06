/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext
import com.cobblemon.mod.common.api.battles.interpreter.Context
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import net.minecraft.text.Text

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
    val context = hashMapOf<Context, Collection<BattleContext>>()
    fun getOppositeSide() = if (this == battle.side1) battle.side2 else battle.side1

    fun broadcastChatMessage(component: Text) {
        return this.actors.forEach { it.sendMessage(component) }
    }
}