package com.cablemc.pokemoncobbled.common.api.pokemon.feature

import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonPropertyType
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound

/**
 * A simple [SpeciesFeature] that is a true/false flag value. It implements [CustomPokemonProperty]
 * to provide a convenient means of registering it with a [CustomPokemonPropertyType]. That can be done
 * smoothly using [FlagSpeciesFeature.registerWithProperty].
 *
 * Implementations of this class don't need to implement anything, but as mentioned in [SpeciesFeature]
 * it's crucial that the same class not be reused for multiple distinct features and making this abstract
 * protects you from yourselves.
 *
 * @author Hiroku
 * @since May 13th, 2022
 */
abstract class FlagSpeciesFeature : SpeciesFeature, CustomPokemonProperty {
    companion object {
        fun registerWithProperty(name: String, clazz: Class<FlagSpeciesFeature>) {
            SpeciesFeature.register(name, clazz)
            CustomPokemonProperty.properties.add(FlagSpeciesFeatureCustomPropertyType(name))
        }
    }

    val name by lazy { SpeciesFeature.getName(this)!! }
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
        enabled = pokemonJSON.get(name)?.asBoolean ?: enabled
        return this
    }

    override fun asString() = "name=$enabled"

    override fun apply(pokemon: Pokemon) {
        pokemon.getFeature<FlagSpeciesFeature>(name)?.enabled = enabled
        pokemon.updateAspects()
    }

    override fun matches(pokemon: Pokemon) = pokemon.getFeature<FlagSpeciesFeature>(name)?.enabled == enabled
}