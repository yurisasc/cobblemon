/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.starter

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.starter.CobblemonStarterHandler
import com.cobblemon.mod.common.util.adapters.PlainJsonTextAdapter
import com.cobblemon.mod.common.util.adapters.pokemonPropertiesShortAdapter
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * The registry for starter Pokémon.
 *
 * The data here is used by the [CobblemonStarterHandler].
 *
 * 3rd party is not enforced to respect this registry.
 */
object StarterRegistry : JsonDataRegistry<StarterCategory> {
    override val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .registerTypeAdapter(Text::class.java, PlainJsonTextAdapter)
        .registerTypeAdapter(PokemonProperties::class.java, pokemonPropertiesShortAdapter)
        .create()
    override val typeToken: TypeToken<StarterCategory> = TypeToken.get(StarterCategory::class.java)
    override val id: Identifier = cobblemonResource("starter")
    override val resourcePath: String = id.path
    override val type: ResourceType = ResourceType.SERVER_DATA
    override val observable: SimpleObservable<out DataRegistry> = SimpleObservable()
    private val categories = hashMapOf<Identifier, StarterCategory>()

    // We let the default starter handler sync the client
    // This is to prevent unnecessary packets depending on impl
    override fun sync(player: ServerPlayerEntity) {}

    override fun reload(data: Map<Identifier, StarterCategory>) {
        this.categories.clear()
        this.categories += data
        Cobblemon.LOGGER.info("Loaded {} starters ({} categories)", this.categories.values.sumOf { it.pokemon.size }, this.categories.size)
    }

    fun all(): Map<Identifier, StarterCategory> = this.categories

    fun keys(): Set<Identifier> = this.categories.keys

    fun categories(): Collection<StarterCategory> = this.categories.values

    fun categoryOf(key: Identifier): StarterCategory? = this.categories[key]

    fun categoryOf(key: String): StarterCategory? = this.categories[key.asIdentifierDefaultingNamespace()]
}