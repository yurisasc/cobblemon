package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.pokemon.egg.EggGroup
import com.google.gson.*
import java.lang.reflect.Type

object EggGroupAdapter : JsonDeserializer<EggGroup>, JsonSerializer<EggGroup> {

    // Safe to just cache
    private val eggGroups = EggGroup.values()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EggGroup {
        val rawID = json.asString
        return this.eggGroups.firstOrNull { eggGroup -> eggGroup.pokeApiID.equals(rawID, true) }
            ?: throw IllegalStateException("Failed to resolve egg group from: $rawID")
    }

    override fun serialize(src: EggGroup, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        // We prettify the enum value instead of PokeAPI format due to it being the "correct" english name
        return JsonPrimitive(
            src.name.lowercase()
                .split("_")
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
        )
    }

}