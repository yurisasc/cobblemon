/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.evolution.adapters

import com.cablemc.pokemod.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemod.common.pokemon.evolution.PokemodLazyPreEvolution
import com.google.gson.*
import java.lang.reflect.Type

object CobbledPreEvolutionAdapter : JsonDeserializer<PreEvolution>, JsonSerializer<PreEvolution> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PreEvolution {
        return PokemodLazyPreEvolution(json.asString)
    }

    override fun serialize(src: PreEvolution, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        if (src.form == src.species.standardForm) {
            return JsonPrimitive(src.species.resourceIdentifier.toString())
        }
        return JsonPrimitive("${src.species.resourceIdentifier} form=${src.form.name}")
    }

}