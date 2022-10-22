/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon.feature

import com.cablemc.pokemod.common.api.pokemon.aspect.AspectProvider
import com.cablemc.pokemod.common.api.pokemon.aspect.SingleConditionalAspectProvider
import com.cablemc.pokemod.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemod.common.api.properties.CustomPokemonPropertyType
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound

/**
 * A simple [SpeciesFeature] that is a true/false flag value. It implements [CustomPokemonProperty]
 * to provide a convenient means of registering it with a [CustomPokemonPropertyType]. That can be done
 * smoothly using [FlagSpeciesFeature.registerWithProperty].
 *
 * Implementations of this class don't need to implement anything.
 *
 * @author Hiroku
 * @since May 13th, 2022
 */
open class FlagSpeciesFeature(override val name: String) : SpeciesFeature, CustomPokemonProperty {
    companion object {
        fun <T : FlagSpeciesFeature> registerWithProperty(name: String, clazz: Class<T>) {
            SpeciesFeature.register(name, clazz)
            CustomPokemonProperty.properties.add(FlagSpeciesFeatureCustomPropertyType(name))
        }

        fun registerWithPropertyAndAspect(name: String) {
            SpeciesFeature.register(name) { FlagSpeciesFeature(name) }
            CustomPokemonProperty.properties.add(FlagSpeciesFeatureCustomPropertyType(name))
            AspectProvider.register(SingleConditionalAspectProvider.getForFeature(name))
        }
    }

    open var enabled = false

    override fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound {
        pokemonNBT.putBoolean(name, enabled)
        return pokemonNBT
    }

    override fun loadFromNBT(pokemonNBT: NbtCompound): SpeciesFeature {
        enabled = if (pokemonNBT.contains(name)) pokemonNBT.getBoolean(name) else enabled
        return this
    }

    override fun saveToJSON(pokemonJSON: JsonObject): JsonObject {
        pokemonJSON.addProperty(name, enabled)
        return pokemonJSON
    }

    override fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature {
        val isEnabled = pokemonJSON.get(name)?.asBoolean
        enabled = isEnabled ?: this.enabled
        return this
    }

    override fun asString() = "$name=$enabled"

    override fun apply(pokemon: Pokemon) {
        pokemon.getFeature<FlagSpeciesFeature>(name)?.enabled = enabled
        pokemon.updateAspects()
    }

    override fun matches(pokemon: Pokemon) = pokemon.getFeature<FlagSpeciesFeature>(name)?.enabled == enabled
}