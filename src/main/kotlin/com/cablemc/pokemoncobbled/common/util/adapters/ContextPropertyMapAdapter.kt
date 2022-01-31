package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.spawning.ContextPropertyMap
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnLoader
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Adapter for handling [ContextPropertyMap]s. This is because a map with an object key
 * doesn't work very cleanly in Gson and we need to apply map adapting to its values anyway.
 *
 * This adapter leverages the [SpawnLoader.deserializingRegisteredSpawnDetail] to know what
 * class to use for deserializing the values of the map.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
object ContextPropertyMapAdapter : JsonSerializer<ContextPropertyMap>, JsonDeserializer<ContextPropertyMap> {
    override fun serialize(map: ContextPropertyMap, type: Type, ctx: JsonSerializationContext): JsonElement {
        val json = JsonObject()
        map.entries.forEach { (key, properties) ->
            json.add(key.name, ctx.serialize(properties, properties::class.java))
        }
        return json
    }

    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): ContextPropertyMap {
        val mapJson = element.asJsonObject
        val map = ContextPropertyMap()
        mapJson.entrySet().forEach { (key, json) ->
            val context = SpawningContext.getByName(key)
            if (context == null) {
                PokemonCobbledMod.LOGGER.error("Unrecognized spawning context type: $key")
                return@forEach
            }

            map[context] = SpawnLoader.deserializingRegisteredSpawnDetail!!.deserializeContextProperty(json, ctx)
        }
        return map
    }

}