/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.materials

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.materials.GoldMaterial
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object GoldMaterials : JsonDataRegistry<List<GoldMaterial>>{
    override val id = cobblemonResource("gold_materials")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<GoldMaterials>()
    override val typeToken: TypeToken<List<GoldMaterial>> =
        TypeToken.getParameterized(List::class.java, GoldMaterial::class.java) as TypeToken<List<GoldMaterial>>
    override val resourcePath = "gold_materials"
    override val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .create()

    private val resourceData = mutableMapOf<Identifier, GoldMaterial>()
    override fun sync(player: ServerPlayerEntity) {}

    override fun reload(data: Map<Identifier, List<GoldMaterial>>) {
        data.forEach { entry ->
            entry.value.forEach {
                resourceData.remove(it.item)
                registerFromData(it.item, it.content)
            }
        }
    }

    private fun registerFromData(identifier: Identifier, value: Int) {
        resourceData[identifier] = GoldMaterial(value, identifier)
    }

    fun isGoldMaterial(item: Identifier): Boolean {
        return item in resourceData.keys
    }

    fun getContent(item: Identifier): Int? {
        return resourceData[item]?.content
    }
}
