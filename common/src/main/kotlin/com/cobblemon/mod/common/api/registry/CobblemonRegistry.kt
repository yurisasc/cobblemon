/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.registry

import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

/**
 * Class containing utility methods for Cobblemon registries.
 *
 * @param T The type of the [Registry] element.
 *
 * @see [CobblemonRegistries].
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class CobblemonRegistry<T> {

    protected val registeredKeys = hashSetOf<ResourceKey<T>>()

    /**
     * @return The associated [Registry].
     */
    abstract fun registry(): Registry<T>

    /**
     * @return The associated [ResourceKey] of the registry.
     */
    abstract fun registryKey(): ResourceKey<Registry<T>>

    /**
     * @return The [ResourceKey]s for this registry provided by Cobblemon.
     */
    fun keys(): Set<ResourceKey<T>> = this.registeredKeys

    /**
     * Create a [ResourceKey] for this [registry].
     *
     * @param name The name aka the path of the [ResourceKey.location]
     * @return The generated [ResourceKey].
     */
    protected fun key(name: String): ResourceKey<T> {
        val key = ResourceKey.create(this.registryKey(), cobblemonResource(name))
        if (!this.registeredKeys.add(key)) {
            throw IllegalStateException("Attempted to create key $key twice")
        }
        return key
    }

}