/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events.riding

import com.cobblemon.mod.common.api.riding.controller.RideController
import net.minecraft.util.Identifier

data class RegisterRidingControllerAdapterEvent(private val map: MutableMap<Identifier, RideController.Deserializer>) {

    fun register(key: Identifier, deserializer: RideController.Deserializer) {
        this.map[key] = deserializer
    }

}
