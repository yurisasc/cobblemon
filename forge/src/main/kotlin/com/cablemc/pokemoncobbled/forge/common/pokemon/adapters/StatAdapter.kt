package com.cablemc.pokemoncobbled.forge.common.pokemon.adapters

import com.cablemc.pokemoncobbled.forge.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.forge.common.pokemon.stats.Stat
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/** Handles JSON adapting between a Stat and its serialized form; its id.*/
object StatAdapter : JsonSerializer<Stat>, JsonDeserializer<Stat> {
    override fun serialize(src: Stat, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Stat {
        return Stats.getStat(json.asString)!! // TODO some form of sleek error handling if the stat has been removed. A dummy stat, potentially. Hiro: Remember UQ tasks
    }
}