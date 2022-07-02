package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeIdentifierCondition
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeLikeCondition
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeTagCondition
import com.google.common.collect.HashBiMap
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * A type adapter for [BiomeLikeCondition]s.
 *
 * @author Hiroku, Licious
 * @since July 2nd, 2022
 */
object BiomeLikeConditionAdapter : JsonDeserializer<BiomeLikeCondition<*>>, JsonSerializer<BiomeLikeCondition<*>> {

    private const val PREFIX = "prefix"
    private const val DATA = "data"

    private val variants = HashBiMap.create<Char, KClass<out BiomeLikeCondition<*>>>()
    private val variantValueTypes = hashMapOf<Char, TypeToken<*>>()

    init {
        @Suppress("UNCHECKED_CAST")
        this.registerVariant(BiomeTagCondition.PREFIX, BiomeTagCondition::class, TypeToken.getParameterized(TagKey::class.java, Biome::class.java) as TypeToken<TagKey<Biome>>)
    }

    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): BiomeLikeCondition<*> {
        val prefix: Char
        val data: JsonElement
        if (element.isJsonObject) {
            val jObject = element.asJsonObject
            prefix = jObject.get(PREFIX).asString.first()
            data = jObject.get(DATA)
        }
        else {
            val rawInput = element.asString
            if (rawInput.isEmpty()) {
                throw IllegalArgumentException("Cannot process a ${BiomeLikeCondition::class.simpleName} from an empty string!")
            }
            prefix = rawInput.first()
            // If this is true we already know it will be our BiomeIdentifierCondition since valid identifier chars cannot be registered
            if (Identifier.isCharValid(prefix)) {
                return BiomeIdentifierCondition(ctx.deserialize(element, Identifier::class.java))
            }
            data = try {
                JsonPrimitive(rawInput.substring(1))
            } catch (_: IndexOutOfBoundsException) {
                throw IllegalArgumentException("Cannot process a string that is empty after the prefix")
            }
        }
        val variantType = this.variants[prefix] ?: throw IllegalArgumentException("Cannot resolve variant type with prefix $prefix")
        val valueType = this.variantValueTypes[prefix] ?: throw IllegalArgumentException("Cannot resolve value type with prefix $prefix")
        return variantType.primaryConstructor!!.call(ctx.deserialize(data, valueType.type))
    }

    override fun serialize(condition: BiomeLikeCondition<*>, type: Type, ctx: JsonSerializationContext): JsonElement {
        if (condition is BiomeIdentifierCondition) {
            return JsonPrimitive(condition.requiredValue.toString())
        }
        val prefix = this.variants.inverse()[condition::class] ?: throw IllegalArgumentException("Cannot resolve variant for type ${condition::class.qualifiedName}")
        val data = ctx.serialize(condition.requiredValue)
        if (data.isJsonPrimitive) {
            return JsonPrimitive("$prefix${data.asString}")
        }
        return JsonObject().apply {
            addProperty(PREFIX, prefix)
            add(DATA, data)
        }
    }

    /**
     * Registers a new variant into this adapter.
     * Char constraints are checked in [BiomeLikeCondition.registerVariant] normally so API wise it's safe.
     *
     * @param T
     * @param prefix
     * @param variantType
     * @param valueType
     */
    internal fun <T : Any> registerVariant(prefix: Char, variantType: KClass<out BiomeLikeCondition<T>>, valueType: TypeToken<T>) {
        if (this.variants.containsKey(prefix)) {
            val current = this.variants[prefix]!!
            PokemonCobbled.LOGGER.warn("Registering colliding prefix '{}' in the BiomeCondition adapter, replacing target {} to {}, things may not work as intended!", prefix, current.qualifiedName, variantType.qualifiedName)
        }
        this.variants[prefix] = variantType
        this.variantValueTypes[prefix] = valueType
    }

}