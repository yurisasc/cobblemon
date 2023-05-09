package com.cobblemon.mod.common.util.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.mojang.datafixers.util.Either
import java.lang.reflect.Type
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.gen.structure.Structure

class EitherIdentifierOrTagAdapter<E, T : Registry<E>>(val registryKey: RegistryKey<T>) : JsonDeserializer<Either<Identifier, TagKey<E>>> {
    override fun deserialize(
        element: JsonElement,
        type: Type,
        ctx: JsonDeserializationContext
    ): Either<Identifier, TagKey<E>> {
        val string = element.asString
        return if (string.startsWith("#")) {
            Either.right(TagKey.of(registryKey, Identifier(string.substring(1))))
        } else {
            Either.left(Identifier(string))
        }
    }
}