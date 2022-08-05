package com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemoncobbled.common.pokemon.evolution.CobbledPreEvolution
import com.cablemc.pokemoncobbled.common.util.asIdentifierDefaultingNamespace
import com.google.gson.*
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