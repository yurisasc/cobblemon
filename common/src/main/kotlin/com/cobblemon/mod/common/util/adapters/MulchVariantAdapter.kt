package com.cobblemon.mod.common.util.adapters

import com.cobblemon.mod.common.api.abilities.AbilityPool
import com.cobblemon.mod.common.api.abilities.PotentialAbility
import com.cobblemon.mod.common.api.mulch.MulchVariant
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object MulchVariantAdapter : JsonDeserializer<MulchVariant> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): MulchVariant {
        val mulchName = json.asString.lowercase()
        return MulchVariant.values().filter { it.name.lowercase() == mulchName }.first()
    }
}
