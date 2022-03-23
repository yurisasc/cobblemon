package com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.adapters.PreEvolutionAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledPreEvolution
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import java.lang.reflect.Type

object CobbledPreEvolutionAdapter : PreEvolutionAdapter {

    private const val SPECIES = "species"
    private const val FORM = "form"

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PreEvolution {
        if (json.isJsonPrimitive) {
            return CobbledPreEvolution(json.asString)
        }
        val jObject = json.asJsonObject
        return CobbledPreEvolution(jObject.get(SPECIES).asString, jObject.get(FORM).asString)
    }

    override fun serialize(src: PreEvolution, typeOfSrc: Type, context: JsonSerializationContext) = JsonObject().apply {
        addProperty(SPECIES, src.species.name)
        addProperty(FORM, src.form.name)
    }

}