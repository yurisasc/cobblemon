package com.cobblemon.mod.common.util.adapters

import com.cobblemon.mod.common.api.pokemon.status.Status
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.google.gson.*
import java.lang.reflect.Type

object StatusAdapter : JsonDeserializer<Status>, JsonSerializer<Status> {
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Status {
        val id = element.asString.asIdentifierDefaultingNamespace()
        val status = Statuses.getStatus(id)
        return status ?: throw IllegalArgumentException("There is no status with the ID $id")
    }

    override fun serialize(status: Status, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(status.name.toString())
    }
}