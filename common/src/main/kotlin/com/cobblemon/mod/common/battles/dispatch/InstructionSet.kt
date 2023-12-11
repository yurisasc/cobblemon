/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.dispatch

import com.cobblemon.mod.common.api.battles.model.PokemonBattle

class InstructionSet {
    var currentParent: ParentInstruction? = null
    val instructions: MutableList<InterpreterInstruction> = mutableListOf()

    fun getSubsequentInstructions(instruction: InterpreterInstruction): List<InterpreterInstruction> {
        val index = instructions.indexOf(instruction)
        return instructions.subList(index + 1, instructions.size).toList()
    }

    fun getPreviousInstructions(instruction: InterpreterInstruction): List<InterpreterInstruction> {
        val index = instructions.indexOf(instruction)
        return instructions.subList(0, index).toList()
    }

    fun execute(battle: PokemonBattle) {
        instructions.forEach { it(battle) }
    }
}