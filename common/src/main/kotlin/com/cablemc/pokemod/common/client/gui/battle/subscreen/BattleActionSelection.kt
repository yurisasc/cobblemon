/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.battle.subscreen

import com.cablemc.pokemod.common.api.gui.ParentWidget
import com.cablemc.pokemod.common.client.battle.SingleActionRequest
import com.cablemc.pokemod.common.client.gui.battle.BattleGUI
import net.minecraft.text.MutableText

abstract class BattleActionSelection(
    val battleGUI: BattleGUI,
    val request: SingleActionRequest,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    name: MutableText
) : ParentWidget(x, y, width, height, name) {
    val opacity: Float
        get() = battleGUI.opacity
}