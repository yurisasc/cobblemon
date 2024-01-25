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
class RidingContext internal constructor(builder: RidingContextBuilder) {

    private val properties: Map<RideControllerPropertyKey<*>, Reference<*>> = builder.properties
    var speed: Float = 0.0F

    fun <T : Any> property(key: RideControllerPropertyKey<T>): T? {
        return this.properties[key]?.reference as T?
    }

    fun <T : Any> propertyOrDefault(key: RideControllerPropertyKey<T>, fallback: T): T {
        return this.property(key) ?: fallback
    }

    fun apply(properties: RideControllerProperties): RidingContext {
        val builder = RidingContextBuilder()
        properties.apply(builder)

        val result = builder.finalize()
        result.speed = this.speed
        return result
    }
}

class RidingContextBuilder {

    internal val properties: MutableMap<RideControllerPropertyKey<*>, Reference<*>> = mutableMapOf()
    private val states: MutableMap<RidingStateKey<*>, Reference<*>> = mutableMapOf()

    fun <T : Any> property(key: RideControllerPropertyKey<T>, value: T): RidingContextBuilder {
        this.properties[key] = Reference(value)
        return this
    }

    fun finalize(): RidingContext {
        return RidingContext(this)
    }
}