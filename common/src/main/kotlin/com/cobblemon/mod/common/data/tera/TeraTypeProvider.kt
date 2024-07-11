/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.data.tera

import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.api.types.tera.ElementalTypeTeraType
import com.cobblemon.mod.common.api.types.tera.StellarTeraType
import com.cobblemon.mod.common.api.types.tera.TeraType
import com.cobblemon.mod.common.data.DataExport
import com.cobblemon.mod.common.data.OutputtingDataProvider
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import java.lang.IllegalStateException
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class TeraTypeProvider(packOutput: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) : OutputtingDataProvider<TeraType, TeraTypeProvider.TeraTypeDataExport>(packOutput, lookupProvider) {

    override fun getName(): String = "tera types"

    override fun pathProvider(): PackOutput.PathProvider = this.createPathForCobblemonData(CobblemonRegistries.TERA_TYPE_KEY)

    override fun buildEntries(lookupProvider: HolderLookup.Provider, consumer: Consumer<TeraTypeDataExport>) {
        val elementalTypeLookup = lookupProvider.lookup(CobblemonRegistries.ELEMENTAL_TYPE_KEY)
            .orElseThrow { IllegalStateException("No provider for ${CobblemonRegistries.ELEMENTAL_TYPE_KEY}") }
        ElementalTypes.keys().forEach { key ->
            elementalTypeLookup.get(key).ifPresent { type ->
                consumer.accept(this.elementalTeraType(type.value()))
            }
        }
        consumer.accept(this.dataExport("stellar", StellarTeraType))
    }

    private fun elementalTeraType(elementalType: ElementalType): TeraTypeDataExport {
        val type = ElementalTypeTeraType(elementalType)
        return this.dataExport(type.resourceLocation().path, type)
    }

    private fun dataExport(name: String, type: TeraType): TeraTypeDataExport {
        return TeraTypeDataExport(cobblemonResource(name), type)
    }

    class TeraTypeDataExport(
        private val id: ResourceLocation,
        private val teraType: TeraType,
    ) : DataExport<TeraType> {
        override fun id(): ResourceLocation = this.id
        override fun codec(): Codec<TeraType> = TeraType.CODEC
        override fun value(): TeraType = this.teraType
    }

}