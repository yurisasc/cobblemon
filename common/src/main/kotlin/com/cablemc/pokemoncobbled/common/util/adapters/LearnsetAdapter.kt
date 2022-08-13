package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.pokemon.Learnset
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object LearnsetAdapter : JsonDeserializer<Learnset> {
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Learnset {
        val array = json.asJsonArray
        val learnset = Learnset()
        for (element in array) {
            var added = false
            interpreterLoop@
            for (interpreter in Learnset.interpreters) {
                if (interpreter.loadMove(element, learnset)) {
                    added = true
                    break@interpreterLoop
                }
            }

            if (!added) {
                LOGGER.error("Unable to load entry from learnset: $element")
            }
        }
        return learnset
    }
}