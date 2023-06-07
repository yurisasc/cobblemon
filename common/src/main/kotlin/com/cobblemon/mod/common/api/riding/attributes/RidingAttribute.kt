/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.attributes

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

data class RidingAttribute(val id: Identifier, val value: Float) {

    companion object {

        @JvmStatic
        val SPEED = cobblemonResource("speed")

        @JvmStatic
        val ACCELERATION = cobblemonResource("acceleration")

        @JvmStatic
        val HANDLING = cobblemonResource("handling")

        @JvmStatic
        val WEIGHT = cobblemonResource("weight")

        @JvmStatic
        val HEIGHT = cobblemonResource("height")

    }
}