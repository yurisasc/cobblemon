/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.fishing

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.net.messages.client.fishing.FishingBaitRegistrySyncPacket
import com.cobblemon.mod.common.registry.ItemTagCondition
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.adapters.ItemLikeConditionAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object FishingBaits : JsonDataRegistry<List<FishingBait>>{
    override val id = cobblemonResource("fishing_baits")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<FishingBaits>()
    override val typeToken: TypeToken<List<FishingBait>> =
        TypeToken.getParameterized(List::class.java, FishingBait::class.java) as TypeToken<List<FishingBait>>
    override val resourcePath = "fishing_baits"
    override val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .registerTypeAdapter(ItemTagCondition::class.java, ItemLikeConditionAdapter)
        .create()

    private val itemMap = mutableMapOf<Identifier, FishingBait>()
    private val tagMap = mutableMapOf<ItemTagCondition, FishingBait>()
    override fun sync(player: ServerPlayerEntity) {
        FishingBaitRegistrySyncPacket(this.itemMap.values.toList() + this.tagMap.values.toList()).sendToPlayer(player)
    }

    override fun reload(data: Map<Identifier, List<FishingBait>>) {
        data.forEach { entry ->
            entry.value.forEach {
                itemMap.remove(it.item)
                if (it.item != null) {
                    itemMap[it.item] = it
                }
                if (it.tag != null) {
                    tagMap[it.tag] = it
                }
            }
        }
    }

    fun isFishingBait(item: ItemStack): Boolean {
        val itemId = Registries.ITEM.getId(item.item)
        return itemId in itemMap.keys || tagMap.keys.any {
            it.fits(item.item, Registries.ITEM)
        }
    }

    fun getBaitEffect(item: ItemStack): String? {
        val itemId = Registries.ITEM.getId(item.item)
        if (itemId in itemMap.keys) {
            return itemMap[itemId]?.effect
        }
        val tag = tagMap.keys.firstOrNull { it.fits(item.item, Registries.ITEM) }
        if (tag != null) {
            return tagMap[tag]?.effect
        }
        return null
    }

    fun getBaitSubcategory(item: ItemStack): String? {
        val itemId = Registries.ITEM.getId(item.item)
        if (itemId in itemMap.keys) {
            return itemMap[itemId]?.subcategory
        }
        val tag = tagMap.keys.firstOrNull { it.fits(item.item, Registries.ITEM) }
        if (tag != null) {
            return tagMap[tag]?.subcategory
        }
        return null
    }

    fun getBaitSuccessChance(item: ItemStack): Double? {
        val itemId = Registries.ITEM.getId(item.item)
        if (itemId in itemMap.keys) {
            return itemMap[itemId]?.chance
        }
        val tag = tagMap.keys.firstOrNull { it.fits(item.item, Registries.ITEM) }
        if (tag != null) {
            return tagMap[tag]?.chance
        }
        return null
    }

    fun getBaitEffectValue(item: ItemStack): Double? {
        val itemId = Registries.ITEM.getId(item.item)
        if (itemId in itemMap.keys) {
            return itemMap[itemId]?.value
        }
        val tag = tagMap.keys.firstOrNull { it.fits(item.item, Registries.ITEM) }
        if (tag != null) {
            return tagMap[tag]?.value
        }
        return null
    }


    fun getBaitNote(item: ItemStack): String? {
        val itemId = Registries.ITEM.getId(item.item)
        if (itemId in itemMap.keys) {
            return itemMap[itemId]?.note
        }
        val tag = tagMap.keys.firstOrNull { it.fits(item.item, Registries.ITEM) }
        if (tag != null) {
            return tagMap[tag]?.note
        }
        return null
    }
}
