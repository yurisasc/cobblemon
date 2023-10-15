/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.conditional

import net.minecraft.registry.Registry
import net.minecraft.registry.tag.TagKey

class RegistryTagCondition<T>(val tag: TagKey<T>) : RegistryLikeCondition<T> {

    override fun fits(value: T, registry: Registry<T>): Boolean = registry.getEntry(value).isIn(this.tag)

    override fun pretty(): String = "#${this.tag.id}"

}