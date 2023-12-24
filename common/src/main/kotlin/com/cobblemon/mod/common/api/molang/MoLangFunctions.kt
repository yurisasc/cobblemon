/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.molang

import com.bedrockk.molang.runtime.MoLangEnvironment
import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.MoParams
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import net.minecraft.block.Block
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.dimension.DimensionType

/**
 * Holds a bunch of useful MoLang trickery that can be used or extended in API
 *
 * @author Hiroku
 * @since October 2nd, 2023
 */
object MoLangFunctions {
    val generalFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()
    val biomeFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()
    val worldFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()
    val dimensionTypeFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()
    val blockFunctions = hashMapOf<String, java.util.function.Function<MoParams, Any>>()

    fun RegistryEntry<Biome>.asBiomeMoLangValue() = asMoLangValue(RegistryKeys.BIOME).addFunctions(biomeFunctions)
    fun RegistryEntry<World>.asWorldMoLangValue() = asMoLangValue(RegistryKeys.WORLD).addFunctions(worldFunctions)
    fun RegistryEntry<Block>.asBlockMoLangValue() = asMoLangValue(RegistryKeys.BLOCK).addFunctions(blockFunctions)
    fun RegistryEntry<DimensionType>.asDimensionTypeMoLangValue() = asMoLangValue(RegistryKeys.DIMENSION_TYPE).addFunctions(dimensionTypeFunctions)

    fun <T> RegistryEntry<T>.asMoLangValue(key: RegistryKey<Registry<T>>): ObjectValue<RegistryEntry<T>> {
        val value = ObjectValue(
            obj = this,
            stringify = { it.key.get().value.toString() }
        )
        value.functions.put("is_in") {
            val tag = TagKey.of(key, Identifier(it.getString(0).replace("#", "")))
            return@put DoubleValue(if (value.obj.isIn(tag)) 1.0 else 0.0)
        }
        value.functions.put("is_of") {
            val identifier = Identifier(it.getString(0))
            return@put DoubleValue(if (value.obj.matchesId(identifier)) 1.0 else 0.0)
        }
        return value
    }

    fun QueryStruct.addStandardFunctions(): QueryStruct {
        functions.putAll(generalFunctions)
        return this
    }

    fun <T : QueryStruct> T.addFunctions(functions: Map<String, java.util.function.Function<MoParams, Any>>): T {
        this.functions.putAll(functions)
        return this
    }

    fun MoLangEnvironment.getQueryStruct(name: String = "query") = structs.getOrPut(name) { QueryStruct(hashMapOf()) } as QueryStruct

    fun MoLangRuntime.setup(): MoLangRuntime {
        environment.getQueryStruct().addStandardFunctions()
        return this
    }
}