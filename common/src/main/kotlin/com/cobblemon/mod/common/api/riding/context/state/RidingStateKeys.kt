/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.context.state

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

object RidingStateKeys {

    val CURRENT_SPEED: RidingStateKey<Float> = create(cobblemonResource("current_speed"))

    fun <T : Any> create(key: Identifier) : RidingStateKey<T> {
        return RidingStateKey(key)
    }

}