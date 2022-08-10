package com.cablemc.pokemoncobbled.common.util.collections

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.google.gson.JsonArray
import kotlin.reflect.KClass

/**
 * Used for unique properties in species that cannot be instanced while species load.
 * Properties are deserialized when needed using [PokemonSpecies.gson].
 *
 * @param T The type of the elements.
 * @property type The [KClass] of type [T].
 *
 * @param values The deserialized [JsonArray].
 *
 * @author Licious
 * @since March 22nd, 2022
 */
class LazySet<T : Any>(
    private val type: KClass<T>,
    values: JsonArray
) : MutableSet<T> {

    private val json = values.toSet()

    private val elements: MutableSet<T> by lazy {
        this.json.map { jsonElement -> PokemonSpecies.gson.fromJson(jsonElement, type.java) }.toMutableSet()
    }

    override fun add(element: T) = this.elements.add(element)

    override fun addAll(elements: Collection<T>) = this.elements.addAll(elements)

    override fun clear() {
        this.elements.clear()
    }

    override fun iterator() = this.elements.iterator()

    override fun remove(element: T) = this.elements.remove(element)

    override fun removeAll(elements: Collection<T>) = this.elements.removeAll(elements.toSet())

    override fun retainAll(elements: Collection<T>) = this.elements.retainAll(elements.toSet())

    override val size: Int
        get() = this.elements.size

    override fun contains(element: T) = this.elements.contains(element)

    override fun containsAll(elements: Collection<T>) = this.elements.containsAll(elements)

    override fun isEmpty() = this.elements.isEmpty()

}