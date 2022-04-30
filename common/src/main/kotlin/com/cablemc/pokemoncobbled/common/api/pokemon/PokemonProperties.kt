package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.properties.CustomPokemonProperty
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.isInt
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.world.World

/**
 * A grouping of typical, selectable properties for a Pokémon. This is serializable
 * on its own and can be used to apply properties to a [Pokemon] or [PokemonEntity],
 * as well as check that a [Pokemon] or [PokemonEntity] matches all the defined
 * properties.
 *
 * Custom property types can be registered using [CustomPokemonProperty.register].
 *
 * Parsing a [PokemonProperties] from text is done using [PokemonProperties.parse]
 *
 * @author Hiroku
 * @since November 23rd, 2021
 */
open class PokemonProperties {
    companion object {
        fun parse(string: String, delimiter: String = " ", delimitStopper: String = "\"", assignmentOperator: String = "="): PokemonProperties {
            val props = PokemonProperties()
            props.originalString = string
            val keyPairs = divideIntoKeyPairs(string, delimiter, delimitStopper, assignmentOperator)
            props.customProperties = CustomPokemonProperty.properties.mapNotNull { property ->
                val matchedKeyPair = keyPairs.find { it.first.lowercase() in property.keys }
                if (matchedKeyPair == null) {
                    if (!property.needsKey) {
                        var savedProperty: CustomPokemonProperty? = null
                        val keyPair = keyPairs.find { keyPair ->
                            savedProperty = property.fromString(keyPair.second)
                            return@find savedProperty != null
                        }
                        if (keyPair != null) {
                            keyPairs.remove(keyPair)
                        }
                        return@mapNotNull savedProperty
                    } else {
                        return@mapNotNull null
                    }
                } else {
                    keyPairs.remove(matchedKeyPair)
                    return@mapNotNull property.fromString(matchedKeyPair.second)
                }
            }.toMutableList()
//            props.gender = Gender.values().toList().parsePropertyOfCollection(keyPairs, listOf("gender"), labelsOptional = true) { it.name.lowercase() }
            props.level = parseIntProperty(keyPairs, listOf("level", "lvl", "l"))
            props.species = parseSpeciesString(keyPairs)
            return props
        }

        private fun divideIntoKeyPairs(string: String, delimiter: String, delimitCounter: String, assignmentOperator: String): MutableList<Pair<String, String?>> {
            val keyPairs = mutableListOf<Pair<String, String?>>()
            val delimited = string.split(delimiter)
            var aggregated: String? = null

            for (sub in delimited) {
                if (aggregated != null && sub.endsWith(delimitCounter)) {
                    aggregated += delimiter + sub.substring(0, sub.length - 1)
                    val key = aggregated.split(assignmentOperator)[0].lowercase()
                    val value = if (aggregated.contains(assignmentOperator)) {
                        aggregated.split(assignmentOperator)[1]
                    } else {
                        null
                    }
                    aggregated = null
                    keyPairs.add(key to value)
                } else if (aggregated == null) {
                    if (sub.contains(assignmentOperator)) {
                        val equalsIndex = sub.indexOf(assignmentOperator)
                        val key = sub.substring(0, equalsIndex).lowercase()
                        var valueComponent = sub.substring(equalsIndex + 1)
                        if (valueComponent.startsWith(delimitCounter)) {
                            valueComponent = valueComponent.substring(1)
                            if (valueComponent.endsWith(delimitCounter)) {
                                valueComponent = valueComponent.substring(0, valueComponent.length - 1)
                                keyPairs.add(key to valueComponent)
                            } else {
                                aggregated = key + assignmentOperator + valueComponent
                            }
                        } else {
                            keyPairs.add(key to valueComponent)
                        }
                    } else {
                        keyPairs.add(sub.lowercase() to null)
                    }
                } else {
                    aggregated += delimiter + sub
                }
            }

            return keyPairs
        }

        private fun getMatchedKeyPair(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): Pair<String, String?>? {
            return keyPairs.findLast { it.first in labels }
        }

        private fun parseIntProperty(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): Int? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels) ?: return null
            val value = matchingKeyPair.second
            if (value == null || !value.isInt()) {
                return null
            } else {

                return value.toInt()
            }
        }

        private fun parseSpeciesString(keyPairs: MutableList<Pair<String, String?>>): String? {
            val matched = getMatchedKeyPair(keyPairs, listOf("species"))
            if (matched != null) {
                val value = matched.second?.lowercase() ?: return null
                return if (value.lowercase() == "random") {
                    "random"
                } else {
                    PokemonSpecies.species.find { it.name.lowercase() == value }?.name
                }
            } else {
                var species: String? = null

                val keyPair = keyPairs.find { pair ->
                    if (pair.second == null && pair.first.lowercase() == "random") {
                        species = "random"
                    } else {
                        species = PokemonSpecies.species.firstOrNull { it.name.lowercase() == pair.first }?.name
                    }
                    return@find species != null
                }

                if (keyPair != null) {
                    keyPairs.remove(keyPair)
                }

                return species
            }
        }

        private fun parseBooleanProperty(keyPairs: MutableList<Pair<String, String?>>, labels: Iterable<String>): Boolean? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels) ?: return null
            keyPairs.remove(matchingKeyPair)
            val value = matchingKeyPair.second?.lowercase()
            return when (value) {
                null -> true
                "true", "yes" -> true
                "false", "no" -> false
                else -> null
            }
        }

        private fun <T> Iterable<T>.parsePropertyOfCollection(
            keyPairs: MutableList<Pair<String, String?>>,
            labels: Iterable<String>,
            labelsOptional: Boolean = false,
            stringer: (T) -> String
        ): T? {
            val matchingKeyPair = getMatchedKeyPair(keyPairs, labels)
            if (matchingKeyPair != null) {
                val value = matchingKeyPair.second?.lowercase()
                if (value == null) {
                    return null
                } else {
                    val matched = find { stringer(it).lowercase() == value }
                    if (matched != null) {
                        keyPairs.remove(matchingKeyPair)
                    }
                    return matched
                }
            } else if (labelsOptional) {
                val keyPair = keyPairs.firstOrNull { pair -> any { stringer(it).lowercase() == pair.first } }
                if (keyPair != null) {
                    keyPairs.remove(keyPair)
                    return first { stringer(it).lowercase() == keyPair.first }
                }
            }
            return null
        }
    }

    var originalString: String? = null

    var species: String? = null
//    var gender: Gender? = null
    var level: Int? = null

    var customProperties = mutableListOf<CustomPokemonProperty>()

    fun apply(pokemon: Pokemon) {
        level?.let { pokemon.level = it }
        species?.let {
            if (it == "random") {
                PokemonSpecies.species.random()
            } else {
                PokemonSpecies.getByName(it) ?: return@let null
            }
        }?.let { pokemon.species = it }
        customProperties.forEach { it.apply(pokemon) }
    }

    fun apply(pokemonEntity: PokemonEntity) {
        level?.let { pokemonEntity.pokemon.level = it }
//        gender?.let { pokemonEntity.pokemon. }
        species?.let {
            if (it == "random") {
                PokemonSpecies.species.random()
            } else {
                PokemonSpecies.getByName(it) ?: return@let null
            }
        }?.let { pokemonEntity.pokemon.species = it }
        customProperties.forEach { it.apply(pokemonEntity) }
    }

    fun matches(pokemon: Pokemon): Boolean {
        level?.takeIf { it != pokemon.level }?.let { return false }
        species?.run {
            val species = if (this == "random") {
                PokemonSpecies.species.random()
            } else {
                PokemonSpecies.getByName(this) ?: return@run
            }

            if (pokemon.species != species) {
                return false
            }
        }
        return customProperties.none { !it.matches(pokemon) }
    }

    fun matches(pokemonEntity: PokemonEntity): Boolean {
        level?.takeIf { it != pokemonEntity.pokemon.level }?.let { return false }
        species?.run {
            val species = if (this == "random") {
                PokemonSpecies.species.random()
            } else {
                PokemonSpecies.getByName(this) ?: return@run
            }

            if (pokemonEntity.pokemon.species != species) {
                return false
            }
        }
        return customProperties.none { !it.matches(pokemonEntity) }
    }

    fun create(): Pokemon {
        return Pokemon().also { apply(it) }.also { it.initialize() }
    }

    fun createEntity(world: World): PokemonEntity {
        return PokemonEntity(world, create())
    }

    fun writeToNBT(): NbtCompound {
        val nbt = NbtCompound()
        originalString?.let { nbt.putString(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT, it) }
        level?.let { nbt.putInt(DataKeys.POKEMON_LEVEL, it) }
//        gender?.let { nbt.putString(DataKeys.POKEMON_GENDER) }
        species?.let { nbt.putString(DataKeys.POKEMON_SPECIES_TEXT, it) }
        val custom = NbtList()
        customProperties.map { NbtString.of(it.asString()) }.forEach { custom.add(it) }
        nbt.put(DataKeys.POKEMON_PROPERTIES_CUSTOM, custom)
        return nbt
    }

    fun readFromNBT(tag: NbtCompound): PokemonProperties {
        originalString = tag.getString(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT)
        level = tag.getInt(DataKeys.POKEMON_LEVEL)
        species = tag.getString(DataKeys.POKEMON_SPECIES_TEXT)
        val custom = tag.getList(DataKeys.POKEMON_PROPERTIES_CUSTOM, NbtElement.STRING_TYPE.toInt())
        // This is kinda gross
        custom.forEach { customProperties.addAll(parse(it.asString()).customProperties) }
        return this
    }

    fun writeToJSON(): JsonObject {
        val json = JsonObject()
        originalString?.let { json.addProperty(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT, it) }
        level?.let { json.addProperty(DataKeys.POKEMON_LEVEL, it) }
//        gender?.let { nbt.putString(DataKeys.POKEMON_GENDER) }
        species?.let { json.addProperty(DataKeys.POKEMON_SPECIES_TEXT, it) }
        val custom = JsonArray()
        customProperties.map { it.asString() }.forEach { custom.add(it) }
        json.add(DataKeys.POKEMON_PROPERTIES_CUSTOM, custom)
        return json
    }

    fun readFromJSON(json: JsonObject): PokemonProperties {
        originalString = json.get(DataKeys.POKEMON_PROPERTIES_ORIGINAL_TEXT)?.asString
        level = json.get(DataKeys.POKEMON_LEVEL)?.asInt
        species = json.get(DataKeys.POKEMON_SPECIES_TEXT)?.asString
        val custom = json.get(DataKeys.POKEMON_PROPERTIES_CUSTOM)?.asJsonArray
        // This is still kinda gross
        custom?.forEach { customProperties.addAll(parse(it.asString).customProperties) }
        return this
    }

    fun copy(): PokemonProperties {
        return readFromJSON(writeToJSON())
    }
}