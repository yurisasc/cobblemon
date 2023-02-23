/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokedex

import com.google.gson.*
import net.minecraft.util.Identifier
import java.lang.reflect.Type

object PokedexEntryTypeAdapter: JsonSerializer<PokedexEntry>, JsonDeserializer<PokedexEntry> {
    override fun serialize(src: PokedexEntry, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.species.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PokedexEntry {
        return if(json.asJsonObject.get("formName") != null) {
            PokedexEntry(Identifier(json.asJsonObject.get("name").asString), arrayListOf(json.asJsonObject.get("formName").asString))
        } else if(json.asJsonObject.get("formNames") != null) {
            PokedexEntry(Identifier(json.asJsonObject.get("name").asString), json.asJsonObject.get("formNames").asJsonArray.map { it.asString }.toMutableList())
        } else {
            PokedexEntry(Identifier(json.asJsonObject.get("name").asString), arrayListOf("normal"))
        }
    }
}