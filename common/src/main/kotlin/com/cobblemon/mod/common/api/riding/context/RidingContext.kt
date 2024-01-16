/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.context

import com.cobblemon.mod.common.api.reference.Reference
import com.cobblemon.mod.common.api.riding.context.state.RidingStateKey
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerProperties
import com.cobblemon.mod.common.api.riding.controller.properties.RideControllerPropertyKey

@Suppress("UNCHECKED_CAST")
class RidingContext {

    private val properties: Map<RideControllerPropertyKey<*>, Reference<*>> = mutableMapOf()
    private val states: MutableMap<RidingStateKey<*>, Reference<*>> = mutableMapOf()

    fun <T : Any> property(key: RideControllerPropertyKey<T>): T? {
        return this.properties[key]?.reference as T?
    }

    fun <T : Any> propertyOrDefault(key: RideControllerPropertyKey<T>, fallback: T): T {
        return this.property(key) ?: fallback
    }

    fun <T : Any> state(key: RidingStateKey<T>): T? {
        return this.states[key]?.reference as T?
    }

    fun <T : Any> stateOrDefault(key: RidingStateKey<T>, fallback: T): T {
        return this.state(key) ?: fallback
    }

    fun controller() {

    }
}