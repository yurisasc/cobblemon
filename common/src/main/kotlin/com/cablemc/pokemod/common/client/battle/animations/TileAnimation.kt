/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.battle.animations

import com.cablemc.pokemod.common.client.battle.ActiveClientBattlePokemon

interface TileAnimation {
    /** Returns true if the animation is done. */
    operator fun invoke(activeBattlePokemon: ActiveClientBattlePokemon, deltaTicks: Float): Boolean
    fun shouldHoldUntilNextAnimation(): Boolean
}