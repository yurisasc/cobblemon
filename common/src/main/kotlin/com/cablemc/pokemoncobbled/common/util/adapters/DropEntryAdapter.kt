package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.drop.DropEntry
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

/**
 * Map adapter that draws from [DropEntry.entryTypes]. The only unusual trait is that it supports
 * a default entry type which will be used if the type is not specified.
 *
 * @author Hiroku
 * @since July 24th, 2022
 */
object DropEntryAdapter : JsonDeserializer<DropEntry> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): DropEntry {
        json as JsonObject
        val entryClass = json.get("type")?.asString
            ?.let { DropEntry.getByName(it) ?: throw IllegalArgumentException("Unrecognized drop entry type: $it") }
            ?: DropEntry.defaultType
        return ctx.deserialize(json, entryClass)
    }
}