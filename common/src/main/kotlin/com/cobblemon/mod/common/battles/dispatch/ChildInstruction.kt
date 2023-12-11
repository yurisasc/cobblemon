/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.dispatch

abstract class ChildInstruction(val parent: ParentInstruction?) : InterpreterInstruction {
    fun getPreviousSiblings() = parent?.getChildrenBefore(this) ?: emptyList()
    fun getNextSiblings() = parent?.getChildrenAfter(this) ?: emptyList()
    fun getSiblings() = parent?.getOtherChildren(this) ?: emptyList()
}