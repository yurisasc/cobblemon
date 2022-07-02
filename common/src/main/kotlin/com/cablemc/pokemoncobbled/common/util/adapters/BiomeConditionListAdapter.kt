package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.spawning.BiomeConditionList
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeIdentifierCondition
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeLikeCondition
import com.cablemc.pokemoncobbled.common.api.spawning.BiomeTagCondition
import com.google.common.collect.HashBiMap
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import net.minecraft.tag.Tag
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

/**
 * A type adapter for [BiomeConditionList].
 *
 * @author Hiroku, Licious
 * @since July 2nd, 2022
 */
object BiomeConditionListAdapter : JsonDeserializer<BiomeConditionList>, JsonSerializer<BiomeConditionList> {

    private val variants = HashBiMap.create<String, KClass<out BiomeLikeCondition<*>>>()
    private val variantValueTypes = hashMapOf<String, TypeToken<*>>()

    init {
        this.registerVariant(BiomeIdentifierCondition.ID, BiomeIdentifierCondition::class, TypeToken.get(Identifier::class.java))
        @Suppress("UNCHECKED_CAST")
        this.registerVariant(BiomeTagCondition.ID, BiomeTagCondition::class, TypeToken.getParameterized(TagKey::class.java, Biome::class.java) as TypeToken<TagKey<Biome>>)
    }

    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): BiomeConditionList {
        val json = element.asJsonObject
        val conditionList = BiomeConditionList()
        for ((key, subElement) in json.entrySet()) {
            val variantType = this.variants[key] ?: throw IllegalArgumentException("Cannot resolve variant type for key $key")
            val valueType = this.variantValueTypes[key] ?: throw IllegalArgumentException("Cannot resolve value type for key $key")
            subElement.asJsonArray.forEach { childElement ->
                conditionList += variantType.primaryConstructor!!.call(ctx.deserialize(childElement, valueType.type))
            }
        }
        return conditionList
    }

    override fun serialize(list: BiomeConditionList, type: Type, ctx: JsonSerializationContext): JsonElement {
        val arrays = hashMapOf<String, JsonArray>()
        list.forEach { condition ->
            val id = this.variants.inverse()[condition::class] ?: throw IllegalArgumentException("Cannot resolve variant for type ${condition::class.qualifiedName}")
            val array = arrays.getOrPut(id) { JsonArray() }
            array.add(ctx.serialize(condition.requiredValue))
        }
        val json = JsonObject()
        arrays.forEach { (id, array) ->
            json.add(id, array)
        }
        return json
    }

    internal fun <T : Any> registerVariant(id: String, variantType: KClass<out BiomeLikeCondition<T>>, valueType: TypeToken<T>) {
        this.variants[id] = variantType
        this.variantValueTypes[id] = valueType
    }

}