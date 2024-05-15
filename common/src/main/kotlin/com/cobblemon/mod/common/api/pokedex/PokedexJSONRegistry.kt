/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.pokedex.adapter.DexDataAdapter
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies.getByIdentifier
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.net.messages.client.data.PokedexSyncPacket
import com.cobblemon.mod.common.pokedex.DexData
import com.cobblemon.mod.common.pokedex.DexPokemonData
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.*

object PokedexJSONRegistry : JsonDataRegistry<DexData> {
    override val id = cobblemonResource("pokedexes")
    override val type = ResourceType.SERVER_DATA

    override val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .registerTypeAdapter(DexData::class.java, DexDataAdapter)
        .create()

    override val typeToken: TypeToken<DexData> = TypeToken.get(DexData::class.java)
    override val resourcePath = "pokedexes"

    override val observable = SimpleObservable<PokedexJSONRegistry>()

    val dexByIdentifier = mutableMapOf<Identifier, DexData>()
    val dexes: MutableCollection<DexData>
        get() = this.dexByIdentifier.values.toMutableList()

    /**
     * Finds a dex by the pathname of their [Identifier].
     * This method exists for the convenience of finding Cobble default dexes.
     * This uses [getByIdentifier] using the [Cobblemon.MODID] as the namespace and the [name] as the path.
     *
     * @param name The path of the species asset.
     * @return The [DexData] if existing.
     */
    fun getByName(name: String) = this.getByIdentifier(cobblemonResource(name))

    /**
     * Finds a [DexData] by its unique [Identifier].
     *
     * @param identifier The unique [DexData.resourceIdentifier] of the [DexData].
     * @return The [DexData] if existing.
     */
    fun getByIdentifier(identifier: Identifier) = this.dexByIdentifier[identifier]

    /**
     * Counts the currently loaded dexes.
     *
     * @return The loaded dex amount.
     */
    fun count() = this.dexByIdentifier.size

    /**
     * Gets a list of dexes in namespace.
     *
     * @return The dex list in namespace.
     */
    fun getDexesInNamespace(namespace: String = Cobblemon.MODID): Collection<DexData> = dexes.filter { it.identifier.namespace == namespace }.toList()

    /**
     * Get a collection of loaded namespaces.
     *
     * @return The collection of loaded namespaces.
     */
    fun getNamespaces() : Collection<String> = dexes.map {it.identifier.namespace}.toSet()

    fun getDexPokemonDataInNamespace(namespace: String = Cobblemon.MODID): Collection<DexPokemonData> {
        val dex = getDexesInNamespace(namespace)
        val speciesList: MutableList<DexPokemonData> = mutableListOf()
        dex.forEach {
            if(it.pokemon_list != null) {
                it.pokemon_list.forEach { dexPokemonData ->
                    speciesList.add(dexPokemonData)
                }
            }
        }

        return speciesList
    }

    fun getSortedDexData(filters: Collection<EntryFilter>): Collection<DexPokemonData> {
        var entries : MutableSet<DexPokemonData> = mutableSetOf()

        val categoryEntries: MutableMap<PokedexEntryCategory, MutableSet<DexPokemonData>> = mutableMapOf()
        for (category in PokedexEntryCategory.values()) {
            categoryEntries[category] = mutableSetOf()
        }

        //Step 1: Get all dexes without a parent, so we can iterate over them
        val dexesWithoutParents : MutableList<Identifier> = mutableListOf()
        dexesWithoutParents.addAll(dexByIdentifier.keys)

        for(identifier in dexByIdentifier.keys) {
            if(dexByIdentifier[identifier] == null) continue
            for (childDex in dexByIdentifier[identifier]!!.contained_dexes) {
                dexesWithoutParents.remove(childDex)
            }
        }

        //Step 2: Each parent dex adds all of their pokemon and recursively adds the child dexes as well
        for(dexIdentifier in dexesWithoutParents) {
            dexByIdentifier[dexIdentifier]?.parseEntries(entries, categoryEntries)
        }

        for(categoryEntry in categoryEntries.values){
            entries.addAll(categoryEntry)
        }

        entries = entries.filter { pokemon ->
            filters.forEach {filter ->
                if(!filter.filter(pokemon)) return@filter false
            }
            return@filter true
        }.toMutableSet()

        return entries
    }

    override fun reload(data: Map<Identifier, DexData>) {
        this.dexes.clear()
        data.forEach { (identifier, dexData) ->
            if(dexData.enabled){
                try {
                    dexData.identifier = identifier
                    this.dexByIdentifier[identifier] = dexData
                } catch(e: Exception) {
                    Cobblemon.LOGGER.error("Failed to load {} Pokedex", identifier, e)
                }
            }
        }

        dexes.addAll(dexByIdentifier.values)
    }

    override fun sync(player: ServerPlayerEntity) {
        PokedexSyncPacket(dexes.toList()).sendToPlayer(player)
    }
}