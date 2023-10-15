/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.conditional

import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

class RegistryElementCondition<T>(val element: T, private val source: Identifier) : RegistryLikeCondition<T> {

    override fun fits(value: T, registry: Registry<T>): Boolean = value == this.element

    override fun pretty(): String = this.source.toString()

}