package com.cobblemon.mod.common.util.codec

import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.api.types.ElementalType
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import java.util.Optional
import kotlin.math.min

/**
 * Various useful [Codec]s.
 */
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