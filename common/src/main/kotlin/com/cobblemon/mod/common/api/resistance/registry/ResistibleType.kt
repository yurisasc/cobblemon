/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.resistance.registry

import com.cobblemon.mod.common.api.resistance.Resistible
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

data class ResistibleType<T : Resistible>(val codec: MapCodec<T>) {
    companion object {
        @JvmStatic
        val REGISTRY: Registry<ResistibleType<*>> = MappedRegistry(
            ResourceKey.createRegistryKey(cobblemonResource("resistible")),
            Lifecycle.stable()
        )

        fun codec(): Codec<Resistible> = REGISTRY.byNameCodec()
            .dispatch("type", Resistible::resistibleType) { type -> type.codec }
    }
}
