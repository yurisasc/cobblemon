/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.controller.properties

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier

object RideControllerPropertyKeys {

    val SPEED: RideControllerPropertyKey<Float> = this.bound(cobblemonResource("speed")) { it.coerceAtLeast(0.0F) }
    val ACCELERATION: RideControllerPropertyKey<Float> = this.bound(cobblemonResource("acceleration")) { it.coerceIn(0.0F, 1.0F) }

    fun <T : Any> key(identifier: Identifier) : RideControllerPropertyKey<T> {
        return RideControllerPropertyKey(identifier)
    }

    fun <T : Any> bound(identifier: Identifier, limiter: (T) -> T) : RideControllerPropertyKey<T> {
        return RideControllerPropertyKey(identifier, limiter)
    }

}