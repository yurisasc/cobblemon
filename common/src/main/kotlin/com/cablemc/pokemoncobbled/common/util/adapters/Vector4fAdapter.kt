/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.util.adapters

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import net.minecraft.util.math.Vector4f

object Vector4fAdapter : JsonDeserializer<Vector4f> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Vector4f {
        json as JsonArray
        return Vector4f(json[0].asFloat, json[1].asFloat, json[2].asFloat, json[3].asFloat)
    }
}