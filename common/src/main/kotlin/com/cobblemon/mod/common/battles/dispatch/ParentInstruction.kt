/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.dispatch

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage

abstract class ParentInstruction(val instructionSet: InstructionSet, val message: BattleMessage) : InterpreterInstruction {
    val childInstructions: MutableList<ChildInstruction> = mutableListOf()

    fun getChildrenAfter(instruction: ChildInstruction): List<ChildInstruction> {
        val index = childInstructions.indexOf(instruction)
        return childInstructions.subList(index + 1, childInstructions.size).toList()
    }

    fun getChildrenBefore(instruction: ChildInstruction): List<ChildInstruction> {
        val index = childInstructions.indexOf(instruction)
        return childInstructions.subList(0, index).toList()
    }

    fun getOtherChildren(instruction: ChildInstruction) = childInstructions.filter { it != instruction }
}