/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.battles.pokemon

import com.cablemc.pokemod.common.api.moves.Move

/**
 * Wrapper for [Move] containing battle only variables
 *
 * @since January 16th, 2022
 * @author Deltric
 */
class BattleMove(val move : Move) {
    var disabled : Boolean = false
}