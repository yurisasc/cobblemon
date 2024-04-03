/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters

import com.google.gson.*
import net.minecraft.text.Text
import java.lang.reflect.Type

object PlainJsonTextAdapter : JsonDeserializer<Text>, JsonSerializer<Text> {
    override fun deserialize(jElement: JsonElement, type: Type, context: JsonDeserializationContext): Text {
        return Text.Serializer.fromJson(jElement) ?: throw IllegalArgumentException("Cannot resolve null text")
    }

    override fun serialize(text: Text, type: Type, context: JsonSerializationContext): JsonElement = JsonPrimitive(Text.Serializer.toJson(text))
}