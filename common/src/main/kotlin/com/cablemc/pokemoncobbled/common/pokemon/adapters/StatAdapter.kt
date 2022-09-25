/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/** Handles JSON adapting between a Stat and its serialized form; its id.*/
object StatAdapter : JsonSerializer<Stat>, JsonDeserializer<Stat> {
    override fun serialize(src: Stat, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Stat {
        return Stats.getStat(json.asString)
    }
}