/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex.adapter

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.berry.GrowthFactor
import com.cobblemon.mod.common.api.pokedex.PokedexEntryCategory
import com.cobblemon.mod.common.pokedex.DexPokemonData
import com.google.gson.*
import net.minecraft.util.Identifier
import java.lang.reflect.Type

object DexPokemonDataAdapter: JsonDeserializer<DexPokemonData>, JsonSerializer<DexPokemonData> {
    override fun deserialize(jElement: JsonElement, type: Type, context: JsonDeserializationContext): DexPokemonData {
        val json = jElement.asJsonObject
        val categoryJson = json.get("category")
        var category = PokedexEntryCategory.STANDARD
        if(categoryJson != null){
            category = PokedexEntryCategory.from(categoryJson.asString) ?: category
        }

        return DexPokemonData(
            name = Identifier(json.get("name").asString),
            forms = json.getAsJsonArray("forms").map { it.asString }.toMutableList(),
            category = category
        )
    }

    override fun serialize(data: DexPokemonData, type: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(data).asJsonObject
    }

}