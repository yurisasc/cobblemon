/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters

import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.MoValue
import com.bedrockk.molang.runtime.value.StringValue
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object MoValueAdapter : JsonDeserializer<MoValue> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): MoValue {
        return if (json.isJsonPrimitive) {
            val prim = json.asJsonPrimitive
            if (prim.isBoolean) {
                DoubleValue(prim.asBoolean)
            } else if (prim.isNumber) {
                DoubleValue(prim.asNumber.toDouble())
            } else {
                StringValue(prim.asString)
            }
        } else {
            throw IllegalArgumentException("Value $json is not a valid MoValue")
        }
    }
}