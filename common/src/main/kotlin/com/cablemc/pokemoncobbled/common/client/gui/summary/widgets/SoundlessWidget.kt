/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.common.api.gui.ParentWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.Text

abstract class SoundlessWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    component: Text
): ParentWidget(pX, pY, pWidth, pHeight, component) {
    /**
     * Do not play sounds when clicking, because clicking a Widget anywhere produces a sound... :(
     */
    override fun playDownSound(pHandler: SoundManager) {
    }

}