/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.codec

import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.api.types.ElementalType
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSyntaxException
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.predicate.NbtPredicate
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * Various useful [Codec] used internally by Cobblemon.
 * These are subject to removal as their need disappears as such it is recommended to avoid using them yourself, but I'm a comment not a cop.
 */
@ApiStatus.Internal
object ExtraCodecs {

    /**
     * A [Codec] for [EntityDimensions].
     */
    @JvmField
    val ENTITY_DIMENSIONS: Codec<EntityDimensions> = RecordCodecBuilder.create { builder ->
        builder.group(
            Codec.FLOAT.fieldOf("width").forGetter(EntityDimensions::width),
            Codec.FLOAT.fieldOf("height").forGetter(EntityDimensions::height),
            Codec.BOOL.fieldOf("fixed").forGetter(EntityDimensions::fixed)
        ).apply(builder, ::EntityDimensions)
    }

    /**
     * A [Codec] for [NbtPredicate].
     * If an empty string is fed it will result in a [NbtPredicate.ANY].
     * Likewise, a [NbtPredicate.ANY] will result in an empty string.
     */
    @JvmField
    val NBT_PREDICATE: Codec<NbtPredicate> = Codec.STRING.flatXmap(
        { string ->
            if (string.isEmpty()) {
                return@flatXmap DataResult.success(NbtPredicate.ANY)
            }
            return@flatXmap try {
                DataResult.success(NbtPredicate.fromJson(JsonPrimitive(string)))
            } catch (e: JsonSyntaxException) {
                DataResult.error { e.message }
            }
        },
        { predicate ->
            val json = predicate.toJson()
            return@flatXmap when {
                predicate == NbtPredicate.ANY -> DataResult.success("")
                json.isJsonNull -> DataResult.error { "Predicate was null" }
                // It's always a NBT in string form.
                else -> DataResult.success(json.asString)
            }
        }
    )

    /**
     * A [Codec] for [ElementalType].
     * This is aimed for stats where a Pokémon has a primary type and a potential secondary.
     * This serializes as a list I.E ["cobblemon:fire", "cobblemon:water"].
     */
    @JvmField
    val DUAL_TYPE_CODEC: Codec<Pair<ElementalType, Optional<ElementalType>>> = Codec.list(this.createRegistryElementCodec(CobblemonRegistries::ELEMENTAL_TYPE))
        .comapFlatMap(
            { list -> this.decodeRangedList(1, 2, list).map { rangedList -> rangedList.first() to Optional.ofNullable(rangedList.getOrNull(1)) } },
            { pair ->
                val list = arrayListOf(pair.first)
                pair.second.ifPresent(list::add)
                list
            }
        ).stable()


    /**
     * A [Codec] for [Box].
     */
    val BOX_CODEC: Codec<Box> = RecordCodecBuilder.create { builder ->
        builder.group(
            Codec.DOUBLE.fieldOf("minX").forGetter(Box::minX),
            Codec.DOUBLE.fieldOf("minY").forGetter(Box::minY),
            Codec.DOUBLE.fieldOf("minZ").forGetter(Box::minZ),
            Codec.DOUBLE.fieldOf("maxX").forGetter(Box::maxX),
            Codec.DOUBLE.fieldOf("maxY").forGetter(Box::maxY),
            Codec.DOUBLE.fieldOf("maxZ").forGetter(Box::maxZ),
        ).apply(builder, ::Box)
    }

    /**
     * Creates a codec that fetches elements from a [Registry].
     * This is normally done with [Registry.getCodec] however neither Forge nor Fabric supports this access early in the lifecycle for custom data registries.
     *
     * @param T The type of the element in the [Registry].
     * @param registryAccessor A supplier for the [Registry].
     * @return A [Codec] of type [T].
     */
    fun <T> createRegistryElementCodec(registryAccessor: () -> Registry<T>): Codec<T> = Identifier.CODEC
        .flatXmap(
            { id ->
                val registry = registryAccessor()
                val item = registry.get(id)
                return@flatXmap if (item == null) DataResult.error { "Failed to find element $id in registry ${registry.key.value}" } else DataResult.success(item)
            },
            { element ->
                val registry = registryAccessor()
                val id = registry.getId(element)
                return@flatXmap if (id == null) DataResult.error { "Failed to resolve ID for $element in registry ${registry.key.value}" } else DataResult.success(id)
            }
        )

    /**
     * TODO
     *
     * @param T
     * @param min
     * @param max
     * @param base
     * @return
     */
    private fun <T> decodeRangedList(min: Int, max: Int, base: List<T>): DataResult<List<T>> = when {
        min > max -> DataResult.error { "Invalid ranged fed [$min-$max]" }
        base.isEmpty() -> DataResult.error { "Empty base given" }
        base.size == max -> DataResult.success(base)
        base.size < min -> DataResult.error({ "List size is ${base.size} minimal required is $min" }, base.subList(0, base.lastIndex))
        else -> DataResult.success(base.subList(0, max - 1))
    }

}