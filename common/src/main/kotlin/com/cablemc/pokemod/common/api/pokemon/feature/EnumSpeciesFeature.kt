/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon.feature

import com.cablemc.pokemod.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.google.gson.JsonObject
import java.util.EnumSet
import net.minecraft.nbt.NbtCompound

/**
 * A [SpeciesFeature] which has a single value from an enum. Extensions of this class
 * just need to provide the set of all entries of that enum.
 *
 * @author Hiroku
 * @since October 13th, 2022
 */
abstract class EnumSpeciesFeature<T : Enum<T>> : SpeciesFeature, CustomPokemonProperty {
    companion object {
        fun <E : Enum<E>, T : EnumSpeciesFeature<E>> registerWithProperty(name: String, clazz: Class<T>) {
            SpeciesFeature.register(name, clazz)
            CustomPokemonProperty.properties.add(EnumSpeciesFeatureCustomPropertyType<E>(name))
        }
    }

    abstract override val name: String
    abstract fun getValues(): EnumSet<T>
    open var enumValue = getValues().random()

    override fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound {
        pokemonNBT.putString(name, enumValue.name)
        return pokemonNBT
    }

    override fun loadFromNBT(pokemonNBT: NbtCompound): SpeciesFeature {
        val valueName = pokemonNBT.getString(name)?.takeIf { it.isNotBlank() } ?: return this
        enumValue = getValues().first { it.name == valueName }
        return this
    }

    override fun saveToJSON(pokemonJSON: JsonObject): JsonObject {
        pokemonJSON.addProperty(name, enumValue.name)
        return pokemonJSON
    }

    override fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature {
        val valueName = pokemonJSON.get(name)?.asString ?: return this
        enumValue = getValues().first { it.name == valueName }
        return this
    }

    override fun asString() = "$name=${enumValue.name}"

    override fun apply(pokemon: Pokemon) {
        pokemon.getFeature<EnumSpeciesFeature<T>>(name)?.enumValue = enumValue
        pokemon.updateAspects()
    }

    override fun matches(pokemon: Pokemon) = pokemon.getFeature<EnumSpeciesFeature<T>>(name)?.enumValue == enumValue
}