package com.cobblemon.mod.common.pokemon.summaryvalue

import com.cobblemon.mod.common.pokemon.summaryvalue.SummaryValue
import com.google.gson.*
import java.lang.reflect.Type

object SummaryValueAdapter : JsonDeserializer<SummaryValue> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SummaryValue {
        val data = json.asJsonObject
        return SummaryValue(data.get("id").asString, data.get("displayName").asString, data.get("maxValue").asInt)
    }
}