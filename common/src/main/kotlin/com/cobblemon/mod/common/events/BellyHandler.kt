/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.events

import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.util.party

object BellyHandler {

    fun onFaint(event : BattleFaintedEvent) {
        event.battle.activePokemon.forEach() {
            if(it.battlePokemon != event.killed) {
                it.battlePokemon?.entity?.pokemon?.loseFullness(1)
            }
        }
    }

    fun onVictory(event : BattleVictoryEvent) {
        event.battle.players.forEach() {
            it.party().forEach() {
                it.loseFullness(1)
            }
        }

        // Redacted code which only made last 2 active pokemon lose fullness
        /*event.battle.activePokemon.forEach() {
            // every pokemon that took part in the battle loses 1 fullness
            it.battlePokemon?.entity?.pokemon?.loseFullness(1)
        }*/

    }
}