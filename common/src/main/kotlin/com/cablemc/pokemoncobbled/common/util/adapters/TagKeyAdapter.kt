package com.cablemc.pokemoncobbled.common.util.adapters

import com.google.gson.*
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import java.lang.reflect.Type

/**
 * An adapter for [TagKey]s.
 * [TagKey]s are just [Identifier]s attached to a certain registry.
 *
 * @param T The type of the [Registry] this [TagKey] belongs to.
 * @property key The [RegistryKey] used to create new [TagKey]s.
 *
 * @author Licious
 * @since July 2nd, 2022
 */
class TagKeyAdapter<T>(private val key: RegistryKey<Registry<T>>) : JsonDeserializer<TagKey<T>>, JsonSerializer<TagKey<T>> {

    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): TagKey<T> {
        val identifier = Identifier(element.asString)
        return TagKey.of(this.key, identifier)
    }

    override fun serialize(tagKey: TagKey<T>, type: Type, ctx: JsonSerializationContext): JsonElement {
        return JsonPrimitive(tagKey.id.toString())
    }

}