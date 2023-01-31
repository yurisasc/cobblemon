/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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