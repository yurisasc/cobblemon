/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.registry

import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

interface CobblemonRegistryElement<T : CobblemonRegistryElement<T>> {

    fun isIn(tag: TagKey<T>): Boolean = this.registryEntry().isIn(tag)

    fun id(): Identifier

    fun registryEntry(): RegistryEntry<T>

    fun registry(): Registry<T>

}