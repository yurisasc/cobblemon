/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution.adapters

import com.cablemc.pokemod.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemod.common.pokemon.evolution.CobbledPreEvolution
import com.cablemc.pokemod.common.util.asIdentifierDefaultingNamespace
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object CobbledPreEvolutionAdapter : JsonDeserializer<PreEvolution>, JsonSerializer<PreEvolution> {

    private const val SPECIES = "species"
    private const val FORM = "form"

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PreEvolution {
        if (json.isJsonPrimitive) {
            return CobbledPreEvolution(json.asString.asIdentifierDefaultingNamespace())
        }
        val jObject = json.asJsonObject
        return CobbledPreEvolution(jObject.get(SPECIES).asString.asIdentifierDefaultingNamespace(), jObject.get(FORM).asString)
    }

    override fun serialize(src: PreEvolution, typeOfSrc: Type, context: JsonSerializationContext) = JsonObject().apply {
        addProperty(SPECIES, src.species.name)
        addProperty(FORM, src.form.name)
    }

}