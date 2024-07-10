/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.registry

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Environment
import com.cobblemon.mod.common.api.pokemon.status.Status
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

@Suppress("unused")
object CobblemonRegistries {

    @JvmStatic
    val ELEMENTAL_TYPE_KEY = this.create<ElementalType>("elemental_type")
    @JvmStatic
    val ELEMENTAL_TYPE get() = this.getRegistry(ELEMENTAL_TYPE_KEY)

    @JvmStatic
    val STATUS_KEY = this.create<Status>("status")

    @JvmStatic
    val STATUS get() = this.getRegistry(STATUS_KEY)

    private fun <T> create(name: String): ResourceKey<Registry<T>> = ResourceKey.createRegistryKey(cobblemonResource(name))

    internal fun register() {
        Cobblemon.implementation.registerBuiltInRegistry(STATUS_KEY, false)
        Cobblemon.implementation.registerDynamicRegistry(ELEMENTAL_TYPE_KEY, ElementalType.CODEC, ElementalType.CODEC)
    }

    private fun <T> getRegistry(key: ResourceKey<Registry<T>>): Registry<T> {
        if (Cobblemon.implementation.environment() == Environment.CLIENT) {
            return Minecraft.getInstance().connection?.registryAccess()?.registryOrThrow(key)
                ?: throw IllegalStateException("Client isn't connected to a world, cannot fetch registry $key")
        }
        return Cobblemon.implementation.server()?.registryAccess()?.registryOrThrow(key)
            ?: throw IllegalStateException("Server hasn't started, cannot fetch registry $key")
    }

}