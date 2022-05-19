package com.cablemc.pokemoncobbled.common.api.pokemon.feature

import com.google.common.collect.HashBiMap
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.api.pokemon.aspect.AspectProvider

/**
 * A piece of state that can be added to some species of Pokémon. Registering an implementing class
 * using [SpeciesFeature.register] adds it as a usable value in the [Species.features] list. All Pokémon
 * are given a default-constructed instance of all SpeciesFeatures mentioned in its species definition.
 *
 * The role of this is to allow species-specific data to be attached to individual Pokémon, such as an alolan
 * flag or a Vivillon pattern variety. This is powerful when combined with [AspectProvider]s.
 *
 * Note: It's important that you don't have two features of the exact same class - the class is used for
 *       reverse lookups so having the same class for different features breaks things.
 *
 * @author Hiroku
 * @since May 13th, 2022
 */
interface SpeciesFeature {
    companion object {
        private val speciesFeatures = HashBiMap.create<String, Class<out SpeciesFeature>>()
        fun <T : SpeciesFeature> register(name: String, clazz: Class<T>) {
            try {
                clazz.getDeclaredConstructor().newInstance()
            } catch (e: Exception) {
                throw IllegalArgumentException("The given species feature class for $name cannot be constructed with a default constructor. You need a default constructor to use species features.")
            }
            speciesFeatures[name] = clazz
        }
        fun unregister(name: String) {
            speciesFeatures.remove(name)
        }
        fun get(name: String) = speciesFeatures[name]
        fun getName(feature: SpeciesFeature) = speciesFeatures.inverse()[feature::class.java]
    }

    fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound
    fun loadFromNBT(pokemonNBT: NbtCompound): SpeciesFeature
    fun saveToJSON(pokemonJSON: JsonObject): JsonObject
    fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature
}