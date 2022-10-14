/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon.effect.adapter

import com.cablemc.pokemod.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemod.common.api.pokemon.effect.ShoulderEffectRegistry
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

object ShoulderEffectAdapter: JsonDeserializer<ShoulderEffect> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ShoulderEffect {
        val (typeId, obj) = if (json.isJsonPrimitive) {
            json.asString to JsonObject()
        } else {
            json.asJsonObject.get("type").asString to json.asJsonObject
        }

        return context.deserialize(obj, ShoulderEffectRegistry.get(typeId))
    }
}