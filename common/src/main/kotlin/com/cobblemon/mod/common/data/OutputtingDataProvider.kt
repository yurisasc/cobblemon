/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.data

import com.cobblemon.mod.common.Cobblemon
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class OutputtingDataProvider<T : Any, E : DataExport<T>>(
    protected val packOutput: PackOutput,
    protected val lookupProvider: CompletableFuture<Provider>
) : DataProvider {

    final override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        return this.lookupProvider.thenCompose { provider -> this.run(cachedOutput, provider) }
    }

    protected fun run(cachedOutput: CachedOutput, lookupProvider: Provider): CompletableFuture<*> {
        val duplicateCheck = hashSetOf<ResourceLocation>()
        val output = arrayListOf<CompletableFuture<*>>()
        val pathProvider = this.pathProvider()
        this.buildEntries(lookupProvider) { dataExport ->
            if (!duplicateCheck.add(dataExport.id())) {
                throw IllegalStateException("Duplicate element in ${this.name}: ${dataExport.id()}")
            }
            output.add(
                DataProvider.saveStable(
                    cachedOutput,
                    lookupProvider,
                    dataExport.codec(),
                    dataExport.value(),
                    pathProvider.json(dataExport.id())
                )
            )
        }
        return CompletableFuture.allOf(*output.toTypedArray())
    }

    protected abstract fun buildEntries(lookupProvider: Provider, consumer: Consumer<E>)

    protected abstract fun pathProvider(): PackOutput.PathProvider

    protected fun <T> createPathForCobblemonRegistryData(key: ResourceKey<Registry<T>>): PackOutput.PathProvider = this.packOutput.createPathProvider(
        PackOutput.Target.DATA_PACK,
        "${Cobblemon.MODID}/${Registries.elementsDirPath(key)}"
    )

    protected fun <T> createPathForCobblemonRegistryAsset(key: ResourceKey<Registry<T>>): PackOutput.PathProvider = this.packOutput.createPathProvider(
        PackOutput.Target.RESOURCE_PACK,
        "${Cobblemon.MODID}/${Registries.elementsDirPath(key)}"
    )
}