/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pasture

import com.cobblemon.mod.common.util.lang
import net.minecraft.client.gui.screen.Screen

class PastureGUI : Screen(lang("pasture.gui.title")) {
    // TODO PASTURE Hey Mr GUI man can you implement this

    override fun init() {
        addDrawableChild(
            PasturePokemonScrollList(
                100,
                100,
                lang("pasture"),
                25
            )
        )
    }
}