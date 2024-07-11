/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.data

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.mixin.accessor.TagsProviderAccessor
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceKey
import java.util.concurrent.CompletableFuture

@Suppress("MemberVisibilityCanBePrivate", "LeakingThis")
abstract class CobblemonRegistryTagsProvider<T>(
    protected val packOutput: PackOutput,
    registryKey: ResourceKey<Registry<T>>,
    lookupProvider: CompletableFuture<HolderLookup.Provider>
) : TagsProvider<T>(packOutput, registryKey, lookupProvider) {

    init {
        (this as TagsProviderAccessor).setPathProvider(this.packOutput.createPathProvider(
            PackOutput.Target.DATA_PACK,
            "tags/${Cobblemon.MODID}/${Registries.elementsDirPath(this.registryKey)}"
        ))
    }

}