package com.cobblemon.mod.common.util.adapters

import com.bedrockk.molang.Expression
import com.bedrockk.molang.MoLang
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object ExpressionAdapter : JsonDeserializer<Expression> {
    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): Expression {
        return MoLang.createParser(json.asString).parseExpression()
    }
}