package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.util.collections.LazySet
import com.google.gson.*
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * The adapter for [LazySet].
 *
 * @param T The type of the elements.
 * @property type The [KClass] of type [T].
 *
 * @author Licious
 * @since March 22nd, 2022
 */
class LazySetAdapter<T : Any>(
    private val type: KClass<T>
) : JsonDeserializer<LazySet<T>>, JsonSerializer<LazySet<T>> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext) = LazySet(type, json.asJsonArray)

    override fun serialize(src: LazySet<T>, typeOfSrc: Type, context: JsonSerializationContext) = JsonArray().apply {
        src.forEach { element -> add(context.serialize(element, type.java)) }
    }

}