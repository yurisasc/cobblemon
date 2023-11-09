/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.fossil

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object NaturalMaterials : JsonDataRegistry<List<NaturalMaterial>>{
    override val id = cobblemonResource("natural_materials")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<NaturalMaterials>()
    override val typeToken: TypeToken<List<NaturalMaterial>> =
        TypeToken.getParameterized(List::class.java, NaturalMaterial::class.java) as TypeToken<List<NaturalMaterial>>
    override val resourcePath = "natural_materials"
    override val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .create()

    private val resourceData = mutableMapOf<Identifier, NaturalMaterial>()
    override fun sync(player: ServerPlayerEntity) {}

    override fun reload(data: Map<Identifier, List<NaturalMaterial>>) {
        data.forEach { entry ->
            entry.value.forEach {
                resourceData.remove(it.item)
                if (it.item != null) {
                    registerFromData(it.item, it.content, it.returnItem)
                }
            }
        }
    }

    private fun registerFromData(identifier: Identifier, value: Int, returnItem: Identifier?) {
        resourceData[identifier] = NaturalMaterial(value, identifier, returnItem)
    }

    fun isNaturalMaterial(item: Identifier): Boolean {
        return item in resourceData.keys
    }

    fun getContent(item: Identifier): Int? {
        return resourceData[item]?.content
    }

    fun getReturnItem(item: Identifier): Identifier? {
        return resourceData[item]?.returnItem
    }
}
