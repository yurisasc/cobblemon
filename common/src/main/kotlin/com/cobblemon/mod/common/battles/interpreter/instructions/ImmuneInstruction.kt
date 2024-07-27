/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter.instructions

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.util.battleLang

/**
 * Format: |-immune|POKEMON
 *
 * POKEMON was immune to a move.
 * @author Hiroku
 * @since October 3rd, 2022
 */
class ImmuneInstruction(override val message: BattleMessage): EffectivenessInstruction {
    override val typeOfEffectiveness: String = "immune"

    override fun invoke(battle: PokemonBattle) {
        battle.dispatchWaiting {
            val pokemon = message.battlePokemon(0, battle) ?: return@dispatchWaiting
            val name = pokemon.getName()
            battle.broadcastChatMessage(battleLang("immune", name).red())
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }
}