/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.net.messages.client.data.PokedexRegistrySyncPacket
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object PokedexDataRegistry : JsonDataRegistry<PokedexAddition> {

    val pokedexes = mutableMapOf<Identifier, PokedexAddition>()

    fun load(identifier: Identifier, pokedex: PokedexAddition) {
        if(pokedexes[identifier] != null) {
            pokedexes[identifier]?.pokemon?.addAll(pokedex.pokemon)
        } else {
            pokedexes[identifier] = pokedex
        }
        pokedex.flattenDex()
    }

    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(PokedexEntry::class.java, PokedexEntryTypeAdapter)
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()
    override val typeToken: TypeToken<PokedexAddition>
        get() = TypeToken.get(PokedexAddition::class.java)
    override val resourcePath: String
        get() = "pokedexes"

    override fun reload(data: Map<Identifier, PokedexAddition>) {
        pokedexes.clear()
        data.forEach() {
            load(it.key, it.value)
        }
    }

    override val id: Identifier
        get() = cobblemonResource("pokedex")
    override val type: ResourceType
        get() = ResourceType.SERVER_DATA
    override val observable: SimpleObservable<out DataRegistry>
        get() = SimpleObservable<PokedexDataRegistry>()

    override fun sync(player: ServerPlayerEntity) {
        PokedexRegistrySyncPacket(this).sendToPlayer(player)
    }
}