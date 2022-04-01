package com.cablemc.pokemoncobbled.common.api.moves.adapters

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Adapter for serializing [MoveTemplate]s by name.
 *
 * @author Hiroku
 * @since April 1st, 2022
 */
object MoveTemplateAdapter : JsonSerializer<MoveTemplate>, JsonDeserializer<MoveTemplate> {
    override fun serialize(template: MoveTemplate, type: Type?, ctx: JsonSerializationContext) = JsonPrimitive(template.name)
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = Moves.getByName(json.asString) ?: Moves.getExceptional()
}