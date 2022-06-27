package com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.adapters.EvolutionAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.variants.ItemInteractionEvolution
import com.cablemc.pokemoncobbled.common.pokemon.evolution.variants.LevelEvolution
import com.cablemc.pokemoncobbled.common.pokemon.evolution.variants.TradeEvolution
import com.google.common.collect.HashBiMap
import com.google.gson.*
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * The default implementation of [EvolutionAdapter].
 *
 * @author Licious
 * @since March 20th, 2022
 */
object CobbledEvolutionAdapter : EvolutionAdapter {

    private const val VARIANT = "variant"

    private val types = HashBiMap.create<String, KClass<out Evolution>>()

    init {
        this.registerType(LevelEvolution.ADAPTER_VARIANT, LevelEvolution::class)
        this.registerType(TradeEvolution.ADAPTER_VARIANT, TradeEvolution::class)
        this.registerType(ItemInteractionEvolution.ADAPTER_VARIANT, ItemInteractionEvolution::class)
    }

    override fun <T : Evolution> registerType(id: String, type: KClass<T>) {
        this.types[id.lowercase()] = type
    }

    override fun deserialize(jsonIn: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Evolution {
        val json = jsonIn.asJsonObject
        val variant = json.get(VARIANT).asString.lowercase()
        val type = this.types[variant] ?: throw IllegalArgumentException("Cannot resolve type for variant $variant")
        return context.deserialize(json, type.java)
    }

    override fun serialize(src: Evolution, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val json = context.serialize(src, src::class.java).asJsonObject
        val variant = this.types.inverse()[src::class] ?: throw IllegalArgumentException("Cannot resolve variant for type ${src::class.qualifiedName}")
        json.addProperty(VARIANT, variant)
        return json
    }

}