/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

interface RegistryElement<T> {

    fun registry(): Registry<T>

    fun resourceKey(): ResourceKey<T>

    fun resourceLocation(): ResourceLocation = this.resourceKey().location()

    fun isTaggedBy(tag: TagKey<T>): Boolean
}