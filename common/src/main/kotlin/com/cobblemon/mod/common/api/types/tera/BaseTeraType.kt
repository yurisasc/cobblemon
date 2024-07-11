/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.tera

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.simplify
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

abstract class BaseTeraType : TeraType {

    override fun registry(): Registry<TeraType> = CobblemonRegistries.TERA_TYPE

    override fun resourceKey(): ResourceKey<TeraType> = this.registry().getResourceKey(this)
        .orElseThrow { IllegalStateException("Unregistered TeraType") }

    override fun isTaggedBy(tag: TagKey<TeraType>): Boolean = this.registry()
        .getHolder(this.resourceKey())
        .orElseThrow { IllegalStateException("Unregistered TeraType") }
        .`is`(tag)

    override fun showdownId(): String {
        return ShowdownIdentifiable.REGEX.replace(this.resourceLocation().simplify().lowercase(), "")
    }
}