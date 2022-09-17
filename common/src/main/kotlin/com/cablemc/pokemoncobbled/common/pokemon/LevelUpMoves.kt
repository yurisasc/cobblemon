/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate

class LevelUpMoves : HashMap<Int, MutableList<MoveTemplate>>() {
    fun getLevelUpMovesUpTo(level: Int) = entries.filter { it.key <= level }.sortedBy { it.key }.flatMap { it.value }.toSet()
}