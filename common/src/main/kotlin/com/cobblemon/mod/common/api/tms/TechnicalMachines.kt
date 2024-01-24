/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tms

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.item.TechnicalMachineItem
import com.cobblemon.mod.common.registry.ItemTagCondition
import com.cobblemon.mod.common.util.adapters.CobblemonObtainMethodAdapter
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object TechnicalMachines : JsonDataRegistry<TechnicalMachine> {
    override val gson = GsonBuilder()
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .registerTypeAdapter(ObtainMethod::class.java, CobblemonObtainMethodAdapter)
        .create()
    override val typeToken = TypeToken.get(TechnicalMachine::class.java)
    override val resourcePath = "tms"
    override val id = cobblemonResource("technical_machines")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<TechnicalMachines>()

    val tmMap = mutableMapOf<Identifier, TechnicalMachine>()

    val tagMap = mutableMapOf<ItemTagCondition, TechnicalMachine>()

    val passiveTms = mutableMapOf<Identifier, TechnicalMachine>()
    override fun reload(data: Map<Identifier, TechnicalMachine>) {
        data.forEach {id, tm ->
            tmMap[id] = tm
            if (tm.obtainMethods.any { it.passive }) passiveTms.put(id, tm)
        }
    }
    override fun sync(player: ServerPlayerEntity) { }

    fun getTechnicalMachineFromStack(item: ItemStack): TechnicalMachine? {
        //val itemId = Registries.ITEM.getId(item.item)
        val itemId = item.nbt?.get("StoredMove")
        /*
        if (itemId in tmMap.keys) {
            return tmMap[itemId]
        }

        //val itemId = item.nbt?.get("StoredMove")



        val tag = tagMap.keys.firstOrNull { it.fits(item.item, Registries.ITEM) }
        if (tag != null) {
            return tagMap[tag]
        }*/
        return null
    }

    fun isTechnicalMachine(item: ItemStack): Boolean {
        val itemId = Registries.ITEM.getId(item.item)

        return (itemId.namespace == "cobblemon" && itemId.path == "technical_machine")
        /*return itemId in tmMap.keys || tagMap.keys.any {
            it.fits(item.item, Registries.ITEM)
        }*/
    }


    fun checkPassives(player: ServerPlayerEntity) {
        val playerTms = Cobblemon.playerData.get(player).tmSet
        passiveTms.forEach { (id, tm) ->
            if (tm.obtainMethods.all { it.matches(player) } && !playerTms.contains(id)) tm.unlock(player)
        }
    }
}