/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon.feature

import com.cablemc.pokemod.common.api.pokemon.aspect.AspectProvider
import com.cablemc.pokemod.common.pokemon.Species
import com.google.common.collect.HashBiMap
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound

/**
 * A piece of state that can be added to some species of Pokémon. Registering an implementing class
 * using [SpeciesFeature.register] adds it as a usable value in the [Species.features] list. All Pokémon
 * are given a default-constructed instance of all SpeciesFeatures mentioned in its species definition.
 *
 * The role of this is to allow species-specific data to be attached to individual Pokémon, such as an alolan
 * flag or a Vivillon pattern variety. This is powerful when combined with [AspectProvider]s.
 *
 * @author Hiroku
 * @since May 13th, 2022
 */
interface SpeciesFeature {
    val name: String
    companion object {
        private val speciesFeatures = HashBiMap.create<String, () -> SpeciesFeature>()
        private val globalSpeciesFeatures = HashBiMap.create<String, () -> SpeciesFeature>()
        fun <T : SpeciesFeature> register(name: String, clazz: Class<T>, global: Boolean = false) {
            try {
                clazz.getDeclaredConstructor().newInstance()
            } catch (e: Exception) {
                throw IllegalArgumentException("The given species feature class for $name cannot be constructed with a default constructor. You need a default constructor to use species features.")
            }
            val instanceJob = { clazz.getDeclaredConstructor().newInstance() }
            speciesFeatures[name] = instanceJob
            if (global) {
                globalSpeciesFeatures[name] = instanceJob
            }
        }
        fun <T : SpeciesFeature> register(name: String, instantiator: () -> T) {
            speciesFeatures[name] = instantiator
        }
        fun <T : SpeciesFeature> registerGlobalFeature(name: String, instantiator: () -> T) {
            this.register(name, instantiator)
            globalSpeciesFeatures[name] = instantiator
        }
        fun unregister(name: String) {
            speciesFeatures.remove(name)
        }
        fun get(name: String) = speciesFeatures[name]
        fun globalFeatures() = this.globalSpeciesFeatures.toMap()
    }

    fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound
    fun loadFromNBT(pokemonNBT: NbtCompound): SpeciesFeature
    fun saveToJSON(pokemonJSON: JsonObject): JsonObject
    fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature
}