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
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.battles.dispatch.InterpreterInstruction

class DeprecatedSplitInstruction(
    val battleActor: BattleActor,
    val publicMessage: BattleMessage,
    val privateMessage: BattleMessage,
    val function: (PokemonBattle, BattleActor, BattleMessage, BattleMessage) -> Unit
) : InterpreterInstruction {
    override fun invoke(battle: PokemonBattle) {
        function(battle, battleActor, publicMessage, privateMessage)
    }
}